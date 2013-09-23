package softwareart.booking.persistence;

import softwareart.booking.BookingService;
import softwareart.booking.Participant;
import softwareart.booking.Workshop;

import java.util.List;

public interface PersistenceService {

    void saveBooking(Participant participant, List<Workshop> workshops);

    void removeBookingFromFile(Participant participant);

    void makeBookingsBasedOnFile(BookingService bookingService);

    void confirm(String mail, Integer... workshopIds);
}
