package softwareart.booking.exceptions;

public class BookingException extends RuntimeException {
    public BookingException() {
    }

    public BookingException(String s) {
        super(s);
    }

    public BookingException(Exception e) {

    }
}
