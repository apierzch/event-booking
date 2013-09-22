package softwareart.booking.persistence;

import softwareart.booking.BookingService;
import softwareart.booking.Workshop;

public interface PersistenceService {

    void saveBooking(String participantMail, Workshop[] workshops);

    void removeBookingFromFile(String mail);

    void makeBookingsBasedOnFile(BookingService bookingService);
}
