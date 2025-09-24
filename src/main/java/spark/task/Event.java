package spark.task;

import spark.storage.Time;

public class Event extends Task {
    protected Time from;
    protected Time to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = new Time(from);
        this.to = new Time(to);
    }

    public Time getFrom() {
        return from;
    }

    public Time getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}