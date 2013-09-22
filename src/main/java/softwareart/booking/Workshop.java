package softwareart.booking;

import softwareart.booking.exceptions.ParticipantsLimitReached;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableCollection;

public class Workshop {
    private Set<String> participants = new HashSet<>();
    private final String title;
    private final int start;
    private final int end;
    private Integer limit = null;

    public Workshop(String title, int start, int end) {
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

    public String getTitle() {
        return title;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Collection<String> getParticipants() {
        return unmodifiableCollection(participants);
    }

    public Workshop limit(int limit) {
        this.limit = limit;
        return this;
    }

    public void addParticipant(String mail) {
        if (limit == null || participants.size() < limit) {
            participants.add(mail);
        } else {
            throw new ParticipantsLimitReached();
        }
    }

    public void removeParticipant(String mail) {
        participants.remove(mail);
    }

    public void removeAllParticipants() {
        participants.clear();
    }
}
