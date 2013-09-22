package softwareart.booking;

import softwareart.booking.exceptions.ParticipantsLimitReached;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableCollection;

public class Workshop {
    private Integer id;
    private Set<Participant> participants = new HashSet<>();
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
        participants.remove(participant);
    }

    public void removeAllParticipants() {
        participants.clear();
    }

    public boolean hasFreePlaces() {
        return limit == null || participants.size() < limit;
    }
}
