package softwareart.booking;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Workshop {
    private Set<String> participants = new HashSet<String>();
    private final String title;
    private final int start;
    private final int end;

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
        return participants;
    }

}
