package softwareart.booking;

import org.junit.Before;
import org.junit.Test;
import softwareart.booking.persistence.FilePersistenceService;

import java.io.*;

import static org.fest.assertions.Assertions.assertThat;


public class BookingIntegrationTest {

    private File file;
    private BookingService service;

    @Test
    public void shouldSaveBooking() throws IOException {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));

        service.book(aParticipant("test@test.com"), 0);

        assertFileLines("test@test.com;testName;false;0");
    }

    private Participant aParticipant(String mail) {
        return new Participant(mail, "testName");
    }

    @Test
    public void shouldAppendBookings() throws IOException {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));

        service.book(aParticipant("test@test.com"), 0);
        service.book(aParticipant("test1@test.com"), 0);

        assertFileLines("test@test.com;testName;false;0",
                "test1@test.com;testName;false;0");
    }

    @Test
    public void shouldNotReplaceBooking() throws IOException {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));
        service.addWorkshop(new Workshop(1, "workshop2", 2, 2));

        service.book(aParticipant("test@test.com"), 0);
        service.book(aParticipant("test2@test.com"), 1);
        service.book(aParticipant("test@test.com"), 1);

        assertFileLines(
                "test@test.com;testName;false;0",
                "test2@test.com;testName;false;1",
                "test@test.com;testName;false;1");
    }

    @Test
    public void shouldConfirmBooking() throws IOException {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));
        service.addWorkshop(new Workshop(1, "workshop2", 2, 2));
        service.book(aParticipant("test@test.com"), 0, 1);

        // when
        service.book(aParticipant("test@test.com"), 0);
        assertFileLines(
                "test@test.com;testName;false;0;1",
                "test@test.com;testName;false;0");

        service.confirm("test@test.com", 0);
        assertFileLines(
                "test@test.com;testName;true;0");
    }

    @Test
    public void shouldReload() throws IOException {
        givenFileWithBookings();
        givenExistingWorkshops();

        // when
        service.reloadBookings();

        assertThat(service.getParticipantsAt(0)).containsOnly(aParticipant("test@test.com"), aParticipant("test2@test.com"));
        assertThat(service.getParticipantsAt(1)).containsOnly(aParticipant("test@test.com"), aParticipant("test1@test.com"), aParticipant("test2@test.com"));
        assertThat(service.getParticipantsAt(2)).containsOnly(aParticipant("test1@test.com"));
        assertThat(service.getParticipantsAt(0).iterator().next().isConfirmed()).isTrue();
        assertThat(service.getParticipantsAt(1).iterator().next().isConfirmed()).isTrue();
        assertThat(service.getParticipantsAt(2).iterator().next().isConfirmed()).isFalse();
    }

    private void givenExistingWorkshops() {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));
        service.addWorkshop(new Workshop(1, "workshop2", 2, 2));
        service.addWorkshop(new Workshop(2, "workshop3", 3, 3));
    }

    private void givenFileWithBookings() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(file), true);
        writer.println("test@test.com;testName;true;0;1");
        writer.println("test1@test.com;testName;false;2;1");
        writer.println("test2@test.com;testName;false;0;1");
        writer.close();
    }

    private void assertFileLines(String... fileLines) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (String fileLine : fileLines) {
                String line = reader.readLine();
                assertThat(line).isEqualTo(fileLine);
            }
            assertThat(reader.readLine()).isNull();
        }
    }

    @Before
    public void setup() throws IOException {
        file = File.createTempFile("softwareart", "booking");
        service = new BookingService(new FilePersistenceService(file));
    }
}
