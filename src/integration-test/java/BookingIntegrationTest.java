import org.junit.Before;
import org.junit.Test;
import softwareart.booking.BookingService;
import softwareart.booking.Workshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class BookingIntegrationTest {

    private File file;
    private BookingService service;

    @Test
    public void shouldSaveBooking() throws IOException {
        service.addWorkshop(new Workshop("workshop1", 1, 1));

        service.book("test@test.com", "workshop1");

        assertFileLines("test@test.com;workshop1");
    }

    @Test
    public void shouldAppendBookings() throws IOException {
        service.addWorkshop(new Workshop("workshop1", 1, 1));

        service.book("test@test.com", "workshop1");
        service.book("test1@test.com", "workshop1");

        assertFileLines("test@test.com;workshop1",
                "test1@test.com;workshop1");
    }

    @Test
    public void shouldReplaceBooking() throws IOException {
        service.addWorkshop(new Workshop("workshop1", 1, 1));
        service.addWorkshop(new Workshop("workshop2", 2, 2));

        service.book("test@test.com", "workshop1");
        service.book("test2@test.com", "workshop2");
        service.book("test@test.com", "workshop2");

        assertFileLines(
                "test2@test.com;workshop2",
                "test@test.com;workshop2");
    }

    private void assertFileLines(String... fileLines) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < fileLines.length; i++) {
            String line = reader.readLine();
            assertThat(line).isEqualTo(fileLines[i]);
        }
        assertThat(reader.readLine()).isNull();
    }

    @Before
    public void setup() throws IOException {
        file = File.createTempFile("softwareart", "booking");
        service = new BookingService(file);
    }
}
