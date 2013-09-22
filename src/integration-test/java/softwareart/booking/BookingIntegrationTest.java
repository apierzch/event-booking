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
        service.addWorkshop(new Workshop("workshop1", 1, 1));

        service.book(aParticipant("test@test.com"), "workshop1");

        assertFileLines("test@test.com;testName;workshop1");
    }

    private Participant aParticipant(String mail) {
        return new Participant(mail, "testName");
    }

    @Test
    public void shouldAppendBookings() throws IOException {
        service.addWorkshop(new Workshop("workshop1", 1, 1));

        service.book(aParticipant("test@test.com"), "workshop1");
        service.book(aParticipant("test1@test.com"), "workshop1");

        assertFileLines("test@test.com;testName;workshop1",
                "test1@test.com;testName;workshop1");
    }

    @Test
    public void shouldReplaceBooking() throws IOException {
        service.addWorkshop(new Workshop("workshop1", 1, 1));
        service.addWorkshop(new Workshop("workshop2", 2, 2));

        service.book(aParticipant("test@test.com"), "workshop1");
        service.book(aParticipant("test2@test.com"), "workshop2");
        service.book(aParticipant("test@test.com"), "workshop2");

        assertFileLines(
                "test2@test.com;testName;workshop2",
                "test@test.com;testName;workshop2");
    }

    @Test
    public void shouldReload() throws IOException {
        givenFileWithBookings();
        givenExistingWorkshops();

        // when
        service.reloadBookings();

        assertThat(service.getParticipantsAt("workshop1")).containsOnly(aParticipant("test@test.com"), aParticipant("test2@test.com"));
        assertThat(service.getParticipantsAt("workshop2")).containsOnly(aParticipant("test@test.com"), aParticipant("test1@test.com"), aParticipant("test2@test.com"));
        assertThat(service.getParticipantsAt("workshop3")).containsOnly(aParticipant("test1@test.com"));
    }

    private void givenExistingWorkshops() {
        service.addWorkshop(new Workshop("workshop1", 1, 1));
        service.addWorkshop(new Workshop("workshop2", 2, 2));
        service.addWorkshop(new Workshop("workshop3", 3, 3));
    }

    private void givenFileWithBookings() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(file), true);
        writer.println("test@test.com;testName;workshop1;workshop2");
        writer.println("test1@test.com;testName;workshop3;workshop2");
        writer.println("test2@test.com;testName;workshop1;workshop2");
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
