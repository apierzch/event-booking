package softwareart.booking;

import com.googlecode.catchexception.CatchException;
import com.googlecode.catchexception.apis.CatchExceptionBdd;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class BookingServiceTest {

    private BookingService service = new BookingService();

    @Test
    public void shouldAddParticipant() {
        service.addWorkshop(new Workshop("workshop", 1, 1));

        // when
        service.book("test@test.com", "workshop");

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
        CatchExceptionBdd.when(service).book("test@test.com", "invalidWorkshop");

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
        CatchExceptionBdd.when(service).book("test@test.com", "workshop1", "workshop2");

        // then
        CatchExceptionBdd.then(CatchException.caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Colliding workshops selected");
    }

    @Test
    public void shouldListCorrectSubset() {
        Workshop workshop1 = new Workshop("workshop1", 1, 1);
        service.addWorkshop(workshop1);
        Workshop workshop2 = new Workshop("workshop2", 2, 2);
        service.addWorkshop(workshop2);
        Workshop workshop3 = new Workshop("workshop3", 3, 3);
        service.addWorkshop(workshop3);
        Workshop workshop4 = new Workshop("workshop4", 1, 2);
        service.addWorkshop(workshop4);
        Workshop workshop5 = new Workshop("workshop5", 2, 3);
        service.addWorkshop(workshop5);
        Workshop workshop6 = new Workshop("workshop6", 1, 3);
        service.addWorkshop(workshop6);

        // when
        Collection<Workshop> workshopsAt1 = service.getWorkshopsStartingAtSlot(1);
        Collection<Workshop> workshopsAt2 = service.getWorkshopsStartingAtSlot(2);
        Collection<Workshop> workshopsAt3 = service.getWorkshopsStartingAtSlot(3);

        // then
        assertThat(workshopsAt1).containsOnly(workshop1, workshop4, workshop6);
        assertThat(workshopsAt2).containsOnly(workshop2, workshop5);
        assertThat(workshopsAt3).containsOnly(workshop3);
    }

    @Test
    public void shouldRemoveParticipantFromPreviousBooking() {
        service.addWorkshop(new Workshop("workshop1", 1, 1));
        service.addWorkshop(new Workshop("workshop2", 2, 2));
        service.book("test@test.com", "workshop1");

        // when
        service.book("test@test.com", "workshop2");

        assertThat(service.getParticipantsAt("workshop1")).isEmpty();
        assertThat(service.getParticipantsAt("workshop2")).containsOnly("test@test.com");
    }
}
