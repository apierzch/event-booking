package softwareart.booking;

import com.googlecode.catchexception.CatchException;
import com.googlecode.catchexception.apis.CatchExceptionBdd;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.*;

@RunWith(JUnitParamsRunner.class)
public class BookingServiceTest {

    private BookingService service = new BookingService();

    @Test
    public void shouldAddParticipant() {
        service.addWorkshop(new Workshop("workshop", 1, 1));

        // when
        service.book("workshop", "test@test.com");

        // then
        assertThat(service.getParticipantsAt("workshop")).containsOnly("test@test.com");
    }

    @Test
    public void newWorkshopShouldHaveNoParticipants() {
        service.addWorkshop(new Workshop("workshop", 1, 1));

        assertThat(service.getParticipantsAt("workshop")).isEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenWorkshopInvalid() {
        service.addWorkshop(new Workshop("workshop", 1, 1));

        // when
        CatchExceptionBdd.when(service).book("invalidWorkshop", "test@test.com");

        // then
        CatchExceptionBdd.then(CatchException.caughtException())
                         .isInstanceOf(IllegalArgumentException.class)
                         .hasMessage("No such workshop");
    }

    @Test
    @Parameters({"1,2, 1,3",
                 "1,3, 2,3",
                 "1,3, 2,2",
                 "2,2, 1,3"})
    public void shouldNotAllowBookingForCollidingWorkshops(int firstStart, int firstEnd, int secondStart, int secondEnd) {
        service.addWorkshop(new Workshop("workshop1", firstStart, firstEnd));
        service.addWorkshop(new Workshop("workshop2", secondStart, secondEnd));

        // when
        service.book("workshop1", "test@test.com");
        CatchExceptionBdd.when(service).book("workshop2", "test@test.com");

        // then
        CatchExceptionBdd.then(CatchException.caughtException())
                         .isInstanceOf(IllegalStateException.class)
                         .hasMessage("Colliding workshops selected");
    }
}
