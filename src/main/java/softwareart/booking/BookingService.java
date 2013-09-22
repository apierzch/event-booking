package softwareart.booking;


import softwareart.booking.exceptions.CollidingWorkshopsException;
import softwareart.booking.exceptions.WorkshopNotFoundException;
import softwareart.booking.persistence.PersistenceService;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BookingService {

    private Map<Integer, Workshop> workshops = new HashMap<>();
    private PersistenceService persistenceService;

    public BookingService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void book(Participant participant, Integer... workshopsIds) {
        Workshop[] workshops = getWorkshops(workshopsIds);

        verifyCollisions(workshops);
        removeBookingFor(participant);
        makeBooking(participant, workshops);
    }

    private void verifyCollisions(Workshop[] workshops) {
        for (Workshop workshop : workshops) {
            for (Workshop testWorkshop : workshops) {
                if (testWorkshop != workshop && workshop.collidesWith(testWorkshop)) {
                    throw CollidingWorkshopsException.forWorkshops(workshops);
                }
            }
        }
    }

    private void makeBooking(Participant participant, Workshop[] workshops) {
        for (Workshop workshop : workshops) {
            workshop.addParticipant(participant);
        }
        persistenceService.saveBooking(participant, workshops);
    }

    private void removeBookingFor(Participant participant) {
        for (Workshop workshop : this.workshops.values()) {
            workshop.removeParticipant(participant);
        }
        persistenceService.removeBookingFromFile(participant);
    }


    private Workshop[] getWorkshops(Integer... workshopIds) {
        Workshop[] workshops = new Workshop[workshopIds.length];
        for (int i = 0; i < workshops.length; i++) {
            workshops[i] = getWorkshop(workshopIds[i]);
        }
        return workshops;
    }

    private Workshop getWorkshop(Integer workshopId) {
        if (!workshops.containsKey(workshopId)) {
            throw new WorkshopNotFoundException("Could not find workshop with name: " + workshopId);
        }
        return workshops.get(workshopId);
    }

    public Collection<Participant> getParticipantsAt(Integer workshopId) {
        return getWorkshop(workshopId).getParticipants();
    }

    public void addWorkshop(Workshop workshop) {
        workshops.put(workshop.getId(), workshop);
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
