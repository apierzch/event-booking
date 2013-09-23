package softwareart.booking;

import softwareart.booking.exceptions.ParticipantsLimitReached;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableCollection;

public class Workshop {
    private Integer id;
    private List<Participant> participants = new ArrayList<>();
    private final String title;
    private final int start;
    private final int end;
    private Integer limit = null;

    public Workshop(int id, String title, int start, int end) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public boolean collidesWith(Workshop workshop) {
        return startsDuring(workshop) || workshop.startsDuring(this);
    }

    private boolean startsDuring(Workshop workshop) {
        return this.start >= workshop.start && this.start <= workshop.end;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Collection<Participant> getParticipants() {
        return unmodifiableCollection(participants);
    }

    public Workshop limit(int limit) {
        this.limit = limit;
        return this;
    }

    public void addParticipant(Participant participant) {
        if (hasFreePlaces()) {
            participants.add(participant);
        } else {
            throw new ParticipantsLimitReached();
        }
    }

    public void removeParticipant(Participant participant) {
        String email = participant.getEmail();
        removeParticipantByMail(email);
    }

    public void removeParticipantByMail(String email) {
        Iterator<Participant> iterator = participants.iterator();
        while (iterator.hasNext()) {
            Participant p = iterator.next();
            if (p.getEmail().equals(email)) {
                iterator.remove();
            }
        }
    }

    public void removeAllParticipants() {
        participants.clear();
    }

    public boolean hasFreePlaces() {
        return limit == null || participants.size() < limit;
    }

    public void ensureOnlyOne(String mail) {
        Iterator<Participant> iterator = participants.iterator();
        boolean firstFound = false;
        while (iterator.hasNext()) {
            Participant p = iterator.next();
            if (p.getEmail().equals(mail)) {
                if (firstFound) {
                    iterator.remove();
                } else {
                    firstFound = true;
                    p.confirm();
                }
            }
        }
    }
}
