package softwareart.booking;


import softwareart.booking.exceptions.CollidingWorkshopsException;
import softwareart.booking.exceptions.WorkshopNotFoundException;
import softwareart.booking.persistence.PersistenceService;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BookingService {

    public static final String SEPARATOR = ";";
    private Map<String, Workshop> workshops = new HashMap<>();
    private PersistenceService persistenceService;

    public BookingService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

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
                    throw new CollidingWorkshopsException();
                }
            }
        }
    }

    private void makeBooking(String participantMail, Workshop[] workshops) {
        for (Workshop workshop : workshops) {
            workshop.addParticipant(participantMail);
        }
        persistenceService.saveBooking(participantMail, workshops);
    }

    private void removeBookingFor(String participantMail) {
        for (Workshop workshop : this.workshops.values()) {
            workshop.removeParticipant(participantMail);
        }
        persistenceService.removeBookingFromFile(participantMail);
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
            throw new WorkshopNotFoundException();
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
        Collection<Workshop> result = new LinkedList<>();
        for (Workshop workshop : workshops.values()) {
            if (workshop.getStart() == slot) {
                result.add(workshop);
            }
        }
        return result;
    }

    public void reloadBookings() {
        for (Workshop workshop : workshops.values()) {
            workshop.removeAllParticipants();
        }

        persistenceService.makeBookingsBasedOnFile(this);
    }

}
