package spark.task;

import spark.storage.Time;

public class Deadline extends Task {
    protected Time by;

    public Deadline(String description, String by) {
        super(description);
        this.by = new Time(by);
    }

    public Time getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}