package softwareart.booking.exceptions;

import softwareart.booking.Workshop;

public class CollidingWorkshopsException extends BookingException {
    public CollidingWorkshopsException(String s) {
        super(s);
    }

    public static CollidingWorkshopsException forWorkshops(Workshop[] workshops) {
        StringBuilder sb = new StringBuilder();
        sb.append("Following workshops collide with each other: ");
        for (Workshop workshop : workshops) {
            sb.append("\"");
            sb.append(workshop);
            sb.append("\", ");
        }
        String message = sb.toString();
        return new CollidingWorkshopsException(message);
    }
}
