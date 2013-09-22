package softwareart.booking;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BookingService {

    private Map<String, Workshop> workshops = new HashMap<String, Workshop>();

    public void book(String participantMail, String... workshopTitles) {
        Workshop[] workshops = getWorkshops(workshopTitles);

        verifyCollisions(workshops);
        removeBookingFor(participantMail);
        makeBooking(participantMail, workshops);
    }

    private void verifyCollisions(Workshop[] workshops) {
        for (Workshop workshop : workshops) {
            for (Workshop testWorkshop : workshops) {
                if (testWorkshop != workshop && workshop.collidesWith(testWorkshop)) {
                    throw new IllegalArgumentException("Colliding workshops selected");
                }
            }
        }
    }

    private void makeBooking(String participantMail, Workshop[] workshops) {
        for (Workshop workshop : workshops) {
            workshop.getParticipants().add(participantMail);
        }
    }

    private void removeBookingFor(String participantMail) {
        for (Workshop workshop : this.workshops.values()) {
            workshop.getParticipants().remove(participantMail);
        }
    }

    private Workshop[] getWorkshops(String[] workshopTitles) {
        Workshop[] workshops = new Workshop[workshopTitles.length];
        for (int i = 0; i < workshops.length; i++) {
            workshops[i] = getWorkshop(workshopTitles[i]);
        }
        return workshops;
    }

    private Workshop getWorkshop(String workshopTitle) {
        if (!workshops.containsKey(workshopTitle)) {
            throw new IllegalArgumentException("No such workshop");
        }
        return workshops.get(workshopTitle);
    }

    public Collection<String> getParticipantsAt(String title) {
        return getWorkshop(title).getParticipants();
    }

    public void addWorkshop(Workshop workshop) {
        workshops.put(workshop.getTitle(), workshop);
    }

    public Collection<Workshop> getWorkshopsStartingAtSlot(int slot) {
        Collection<Workshop> result = new LinkedList<Workshop>();
        for (Workshop workshop : workshops.values()) {
            if (workshop.getStart() == slot) {
                result.add(workshop);
            }
        }
        return result;
    }
}
