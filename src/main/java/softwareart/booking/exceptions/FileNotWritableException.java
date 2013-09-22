package softwareart.booking.exceptions;

public class FileNotWritableException extends BookingException {
    public FileNotWritableException(Exception e) {
        super(e);
    }
}
