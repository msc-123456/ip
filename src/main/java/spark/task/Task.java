package spark.task;

/**
 * Represents a base task with description and completion status.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Construct a new task with the given description.
     *
     * @param description The description for the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Sets the task as not completed.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Gets the task description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the status icon representing completion state.
     *
     * @return "X" if done, " " if not done.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the string representation of the task.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
