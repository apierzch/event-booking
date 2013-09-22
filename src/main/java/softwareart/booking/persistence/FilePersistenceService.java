package softwareart.booking.persistence;

import softwareart.booking.BookingService;
import softwareart.booking.Participant;
import softwareart.booking.Workshop;
import softwareart.booking.exceptions.FileNotReadableException;
import softwareart.booking.exceptions.FileNotRemovableException;
import softwareart.booking.exceptions.FileNotWritableException;

import java.io.*;

public class FilePersistenceService implements PersistenceService {
    private File file;

    public FilePersistenceService(File file) {
        this.file = file;
    }

    @Override
    public void saveBooking(Participant participant, Workshop[] workshops) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(participant.getEmail());
            writer.write(BookingService.SEPARATOR);
            writer.write(participant.getName());
            for (Workshop workshop : workshops) {
                writer.write(BookingService.SEPARATOR);
                writer.write(workshop.getTitle());
            }
            writer.newLine();
        } catch (IOException e) {
            throw new FileNotWritableException(e);
        }
    }

    @Override
    public void removeBookingFromFile(Participant participant) {
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try (BufferedReader br = new BufferedReader(new FileReader(file)); PrintWriter pw = new PrintWriter(new FileWriter(tempFile), true)) {
            String line;
            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {
                if (!line.trim().startsWith(participant.getEmail())) {
                    pw.println(line);
                }
            }
        } catch (IOException ex) {
            // should not happen?
            ex.printStackTrace();
        }

        if (!file.delete()) {
            throw new FileNotRemovableException();
        }
        tempFile.renameTo(file);
    }

    @Override
    public void makeBookingsBasedOnFile(BookingService bookingService) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] booking = line.split(";");
                String[] workshops = new String[booking.length - 2];
                Participant participant = new Participant(booking[0], booking[1]);
                System.arraycopy(booking, 2, workshops, 0, workshops.length);
                bookingService.book(participant, workshops);
            }
        } catch (IOException e) {
            throw new FileNotReadableException(e);
        }
    }

}
