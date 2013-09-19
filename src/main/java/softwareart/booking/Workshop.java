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

    public String getTitle() {
        return title;
    }

    public Collection<String> getParticipants() {
        return participants;
    }

    public boolean collidesWith(Workshop workshop) {
        return startDuring(workshop) || workshop.startDuring(this);
    }

    private boolean startDuring(Workshop workshop) {
        return this.start >= workshop.start && this.start <= workshop.end;
    }

}
