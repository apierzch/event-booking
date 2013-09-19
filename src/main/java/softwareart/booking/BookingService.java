package softwareart.booking;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BookingService {

    private Map<String, Workshop> workshops = new HashMap<String, Workshop>();

    public void book(String workshopTitle, String participantMail) {
        Collection<Workshop> collidingWorkshops = getCollidingWorkshops(getWorkshop(workshopTitle));
        for (Workshop workshop : collidingWorkshops) {
            if(workshop.getParticipants().contains(participantMail)) {
                throw new IllegalStateException("Colliding workshops selected");
            }
        }
        getParticipantsAt(workshopTitle).add(participantMail);
    }

    private Workshop getWorkshop(String workshopTitle) {
        if(!workshops.containsKey(workshopTitle)) {
            throw new IllegalArgumentException("No such workshop");
        }
        return workshops.get(workshopTitle);
    }

    public Collection<String> getParticipantsAt(String title) {
        return getWorkshop(title).getParticipants();
    }

    private Collection<Workshop> getCollidingWorkshops(Workshop workshop) {
        LinkedList<Workshop> result = new LinkedList<Workshop>();
        for (Workshop element : workshops.values()) {
            if(workshop.collidesWith(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public void addWorkshop(Workshop workshop) {
        workshops.put(workshop.getTitle(), workshop);
    }
}
