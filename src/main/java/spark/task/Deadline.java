package spark.task;

import spark.storage.Time;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    protected Time by;

    /**
     * Constructs a new Deadline task.
     *
     * @param description The task description.
     * @param by The task deadline string.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = new Time(by);
    }

    /**
     * Gets the deadline time of this task.
     *
     * @return The Time object representing the deadline.
     */
    public Time getBy() {
        return by;
    }

    /**
     * Returns the string representation of the deadline task.
     *
     * @return The string representation of the deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}