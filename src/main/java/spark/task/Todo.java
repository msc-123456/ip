package spark.task;

/**
 * Represents the basic type of tasks with just description.
 */
public class Todo extends Task {
    /**
     * Constructs a new Todo task.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of the task.
     *
     * @return The string representation of the task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
