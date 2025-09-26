package spark.task;

import spark.storage.Time;

/**
 * Represents an event task with a start time and end time.
 */
public class Event extends Task {
    protected Time from;
    protected Time to;

    /**
     * Constructs a new Event task.
     *
     * @param description The event description.
     * @param from The start time string.
     * @param to The end time string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = new Time(from);
        this.to = new Time(to);
    }

    /**
     * Gets the start time of the event.
     *
     * @return The Time object representing the start time.
     */
    public Time getFrom() {
        return from;
    }

    /**
     * Gets the end time of the event.
     *
     * @return The Time object representing the end time.
     */
    public Time getTo() {
        return to;
    }

    /**
     * Returns the string representation of the event task.
     *
     * @return The string representation of the event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}