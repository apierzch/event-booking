package softwareart.booking;

import com.googlecode.catchexception.CatchException;
import com.googlecode.catchexception.apis.CatchExceptionBdd;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import softwareart.booking.exceptions.CollidingWorkshopsException;
import softwareart.booking.exceptions.ParticipantsLimitReached;
import softwareart.booking.exceptions.WorkshopNotFoundException;
import softwareart.booking.persistence.PersistenceService;

import java.util.Collection;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(JUnitParamsRunner.class)
public class BookingServiceTest {

    private BookingService service;

    @Test
    public void shouldAddParticipant() {
        service.addWorkshop(new Workshop(0, "workshop", 1, 1));

        // when
        service.book(participantForMail("test@test.com"), 0);

        // then
        assertThat(service.getParticipantsAt(0)).containsOnly(participantForMail("test@test.com"));
    }

    private Participant participantForMail(String mail) {
        return new Participant(mail, null);
    }

    @Test
    public void newWorkshopShouldHaveNoParticipants() {
        service.addWorkshop(new Workshop(0, "workshop", 1, 1));

        assertThat(service.getParticipantsAt(0)).isEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenWorkshopInvalid() {
        service.addWorkshop(new Workshop(0, "workshop", 1, 1));

        // when
        CatchExceptionBdd.when(service).book(participantForMail("test@test.com"), 1);

        // then
        CatchExceptionBdd.then(CatchException.caughtException())
                .isInstanceOf(WorkshopNotFoundException.class);
    }

    @Test
    @Parameters({"1,2, 1,3",
            "1,3, 2,3",
            "1,3, 2,2",
            "2,2, 1,3"})
    public void shouldNotAllowBookingForCollidingWorkshops(int firstStart, int firstEnd, int secondStart, int secondEnd) {
        service.addWorkshop(new Workshop(0, "workshop1", firstStart, firstEnd));
        service.addWorkshop(new Workshop(1, "workshop2", secondStart, secondEnd));

        // when
        CatchExceptionBdd.when(service).book(participantForMail("test@test.com"), 0, 1);

        // then
        CatchExceptionBdd.then(CatchException.caughtException())
                .isInstanceOf(CollidingWorkshopsException.class);
    }

    @Test
    public void shouldListCorrectSubset() {
        Workshop workshop1 = new Workshop(0, "workshop1", 1, 1);
        service.addWorkshop(workshop1);
        Workshop workshop2 = new Workshop(1, "workshop2", 2, 2);
        service.addWorkshop(workshop2);
        Workshop workshop3 = new Workshop(2, "workshop3", 3, 3);
        service.addWorkshop(workshop3);
        Workshop workshop4 = new Workshop(3, "workshop4", 1, 2);
        service.addWorkshop(workshop4);
        Workshop workshop5 = new Workshop(4, "workshop5", 2, 3);
        service.addWorkshop(workshop5);
        Workshop workshop6 = new Workshop(5, "workshop6", 1, 3);
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
    public void shouldNotRemoveParticipantFromPreviousBooking() {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));
        service.addWorkshop(new Workshop(1, "workshop2", 2, 2));
        service.book(participantForMail("test@test.com"), 0);

        // when
        service.book(participantForMail("test@test.com"), 1);

        assertThat(service.getParticipantsAt(0)).containsOnly(participantForMail("test@test.com"));
        assertThat(service.getParticipantsAt(1)).containsOnly(participantForMail("test@test.com"));
    }

    @Test
    public void shouldRemoveParticipantFromPreviousBookingWhenConfirmed() {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));
        service.addWorkshop(new Workshop(1, "workshop2", 2, 2));
        service.book(participantForMail("test@test.com"), 0);

        // when
        service.book(participantForMail("test@test.com"), 1);
        service.confirm("test@test.com", 1);

        assertThat(service.getParticipantsAt(0)).isEmpty();
        assertThat(service.getParticipantsAt(1)).containsOnly(participantForMail("test@test.com"));
    }

    @Test
    public void shouldLeaveOnlyOneInWorkshopWhenConfimed() {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1));
        service.addWorkshop(new Workshop(1, "workshop2", 2, 2));
        service.book(participantForMail("test@test.com"), 0, 1);

        // when
        service.book(participantForMail("test@test.com"), 0);
        assertThat(service.getParticipantsAt(0)).hasSize(2);
        assertThat(service.getParticipantsAt(1)).hasSize(1);

        service.confirm("test@test.com", 0);
        assertThat(service.getParticipantsAt(0)).hasSize(1);
        assertThat(service.getParticipantsAt(1)).hasSize(0);
        assertThat(service.getParticipantsAt(0).iterator().next().isConfirmed()).isTrue();
    }

    @Test
    public void shouldAllowForParticipantLimits() {
        service.addWorkshop(new Workshop(0, "workshop1", 1, 1).limit(1));

        service.book(participantForMail("test@test.com"), 0);
        CatchExceptionBdd.when(service).book(participantForMail("test2@test.com"), 0);

        CatchExceptionBdd.then(CatchException.caughtException())
                .isInstanceOf(ParticipantsLimitReached.class);

    }

    @Before
    public void setup() {
        service = new BookingService(mock(PersistenceService.class));
    }
}
