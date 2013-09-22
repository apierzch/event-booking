package softwareart.booking.persistence;

import softwareart.booking.BookingService;
import softwareart.booking.Participant;
import softwareart.booking.Workshop;

public interface PersistenceService {

    void saveBooking(Participant participant, Workshop[] workshops);

    void removeBookingFromFile(Participant participant);

    void makeBookingsBasedOnFile(BookingService bookingService);
}
