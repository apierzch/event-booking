package softwareart.booking.exceptions;

public class FileNotReadableException extends BookingException {
    public FileNotReadableException(Exception e) {
        super(e);
    }
}
