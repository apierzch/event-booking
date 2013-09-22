package softwareart.booking;


import softwareart.booking.exceptions.*;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BookingService {

    public static final String SEPARATOR = ";";
    private Map<String, Workshop> workshops = new HashMap<String, Workshop>();
    private File file;

    public BookingService() {
        file = new File(System.getProperty("user.home") + "/workshop-booking.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new FileNotCreatedException("Could not create file in user.home. Use constructor with File argument.");
        }
    }

    public BookingService(File file) {
        this.file = file;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(participantMail);
            for (Workshop workshop : workshops) {
                workshop.getParticipants().add(participantMail);
                writer.write(SEPARATOR);
                writer.write(workshop.getTitle());
            }
            writer.newLine();
        } catch (IOException e) {
            throw new FileNotWritableException(e);
        }
    }

    private void removeBookingFor(String participantMail) {
        for (Workshop workshop : this.workshops.values()) {
            workshop.getParticipants().remove(participantMail);
        }
        removeBookingFromFile(file, participantMail);
    }

    public void removeBookingFromFile(File inFile, String mail) {

        File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
        try (BufferedReader br = new BufferedReader(new FileReader(file)); PrintWriter pw = new PrintWriter(new FileWriter(tempFile))) {
            String line = null;
            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {
                if (!line.trim().startsWith(mail)) {
                    pw.println(line);
                    pw.flush();
                }
            }
        } catch (IOException ex) {
            // should not happen?
            ex.printStackTrace();
        }

        if (!inFile.delete()) {
            throw new FileNotRemovableException();
        }
        tempFile.renameTo(inFile);
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
        Collection<Workshop> result = new LinkedList<Workshop>();
        for (Workshop workshop : workshops.values()) {
            if (workshop.getStart() == slot) {
                result.add(workshop);
            }
        }
        return result;
    }
}
