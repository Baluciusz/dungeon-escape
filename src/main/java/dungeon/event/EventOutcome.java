package dungeon.event;

/**
 * The result of resolving an event: a message for the player and,
 * optionally, another event that happens immediately afterwards
 * (for example an enemy that was hiding in a camp).
 */
public class EventOutcome {

    private final String message;
    private final GameEvent followUpEvent;

    private EventOutcome(String message, GameEvent followUpEvent) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Outcome message must not be empty");
        }
        this.message = message;
        this.followUpEvent = followUpEvent;
    }

    /** An outcome that ends the event. */
    public static EventOutcome of(String message) {
        return new EventOutcome(message, null);
    }

    /** An outcome that is followed by another event. */
    public static EventOutcome withFollowUp(String message, GameEvent followUpEvent) {
        if (followUpEvent == null) {
            throw new IllegalArgumentException("Follow-up event must not be null");
        }
        return new EventOutcome(message, followUpEvent);
    }

    public String getMessage() {
        return message;
    }

    public boolean hasFollowUp() {
        return followUpEvent != null;
    }

    /** Returns the follow-up event, or null if there is none. */
    public GameEvent getFollowUpEvent() {
        return followUpEvent;
    }
}
