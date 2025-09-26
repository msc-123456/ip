package spark.process_input;

import spark.storage.Collection;
import spark.storage.Time;

/**
 * Provides validation and error handling for user commands.
 */
public class SparkException {
    public static final int LEN_TODO = "todo".length();
    public static final int LEN_DEADLINE = "deadline".length();
    public static final int LEN_EVENT = "event".length();
    public static final int LEN_FROM = "/from".length();
    public static final int LEN_TO = "/to".length();

    private static final String NO_TASKS = "You don't have any tasks yet. Try to create one~";
    private static final String ERROR_INDEX = "Sorry, I cannot find this task. Please try a number within ";

    private static final String TODO_FORMAT = "Please use: todo <task name>";

    private static final String DEADLINE_FORMAT = "Please use: deadline <task name> /by <time>";
    private static final String DEADLINE_NAME = "Oh, I need a name for the deadline task~";
    private static final String DEADLINE_TIME = "Hi! When is the deadline for this task?";

    private static final String EVENT_FORMAT = "Please use: event <task name> /from <time> /to <time>";
    private static final String EVENT_NAME = "Oh, I need a name for the event task~";
    private static final String EVENT_START_TIME = "Hi! When is the start time of this task?";
    private static final String EVENT_END_TIME = "Hi! When is the end time of this task?";
    private static final String TIME_FORMAT_CONFLICT = "Please use the same format for the start time and end time";
    private static final String TIME_CONFLICT = "Sorry, the start time should be earlier than the end time";

    private static final String UNKNOWN_COMMAND = "Sorry, I can't understand your command. ( 0.0 )\n" +
            "Please use these command ( ^_^ ):\n" +
            "todo, deadline, event, mark, unmark, list, bye, delete, finddate, schedule, find";


    /**
     * Validates mark and unmark command syntax.
     *
     * @param input The user input.
     * @return Error message if validation fails, null if valid.
     */
    public static String checkMarkUnmark(String input) {
        int taskCount = Collection.getTaskCount();

        String[] parts = input.split(" ");
        if (parts.length < 2) {
            return "Please use: " + parts[0] + " <number>";
        }
        try {
            int index = Integer.parseInt(parts[1]) - 1;
            if (index < 0 || index >= taskCount) {
                if (taskCount == 0) {
                    return NO_TASKS;
                }else {
                    return ERROR_INDEX + taskCount + " ~";
                }
            }
        } catch (NumberFormatException e) {
            return "Please use: " + parts[0] + " <number>";
        }
        return null;
    }

    /**
     * Validates delete command syntax.
     *
     * @param input The user input.
     * @return Error message if validation fails, null if valid.
     */
    public static String checkDelete(String input) {
        return checkMarkUnmark(input); //Exactly the same logic
    }

    /**
     * Validates todo command syntax and parameters.
     *
     * @param input The user input.
     * @return Error message if validation fails, null if valid.
     */
    public static String checkTodo(String input) {
        if (input.length() <= LEN_TODO) {
            return TODO_FORMAT;
        }
        String description = input.substring(LEN_TODO).trim();
        if (description.isEmpty()) {
            return TODO_FORMAT;
        }
        return null;
    }

    /**
     * Validates deadline command syntax and parameters.
     *
     * @param input The user input.
     * @return Error message if validation fails, null if valid.
     */
    public static String checkDeadline(String input) {
        if (!input.contains("/by")) {
            return DEADLINE_FORMAT;
        }

        if (input.indexOf("/by") <= LEN_DEADLINE) {
            return DEADLINE_NAME + "\n" + DEADLINE_FORMAT;
        }

        String[] parts = input.split("/by", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return DEADLINE_TIME + "\n" + DEADLINE_FORMAT;
        }

        String beforeBy = parts[0].substring(LEN_DEADLINE).trim();
        if (beforeBy.isEmpty()) {
            return DEADLINE_NAME + "\n" + DEADLINE_FORMAT;
        }

        String timeError = checkTimeFormat(parts[1].trim());
        if (timeError != null) {
            return timeError;
        }

        return null;
    }

    /**
     * Validates event command syntax and parameters.
     *
     * @param input The user input.
     * @return Error message if validation fails, null if valid.
     */
    public static String checkEvent(String input) {
        String formatError = checkEventFormat(input);
        if (formatError != null) {
            return formatError;
        }

        String contentError = checkEventContent(input);
        if (contentError != null) {
            return contentError;
        }

        String timeError = checkEventTimes(input);
        if (timeError != null) {
            return timeError;
        }

        return null;
    }

    private static String checkEventFormat(String input) {
        if (!input.contains("/from") || !input.contains("/to")) {
            return EVENT_FORMAT;
        }

        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");
        if (fromIndex > toIndex) {
            return EVENT_FORMAT;
        }

        return null;
    }

    private static String checkEventContent(String input) {
        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");

        String beforeFrom = input.substring(LEN_EVENT, fromIndex).trim();
        if (beforeFrom.isEmpty()) {
            return EVENT_NAME + "\n" +  EVENT_FORMAT;
        }

        String betweenFromAndTo = input.substring(fromIndex + LEN_FROM, toIndex).trim();
        if (betweenFromAndTo.isEmpty()) {
            return EVENT_START_TIME + "\n" +  EVENT_FORMAT;
        }

        String afterTo = input.substring(toIndex + LEN_TO).trim();
        if (afterTo.isEmpty()) {
            return EVENT_END_TIME + "\n" +  EVENT_FORMAT;
        }

        return null;
    }

    private static String checkEventTimes(String input) {
        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");
        String betweenFromAndTo = input.substring(fromIndex + LEN_FROM, toIndex).trim();
        String afterTo = input.substring(toIndex + LEN_TO).trim();

        String fromTimeError = checkTimeFormat(betweenFromAndTo);
        if (fromTimeError != null) {
            return fromTimeError;
        }
        String toTimeError = checkTimeFormat(afterTo);
        if (toTimeError != null) {
            return toTimeError;
        }

        Time startTime = new Time(betweenFromAndTo);
        Time endTime = new Time(afterTo);

        if (startTime.hasTime() != endTime.hasTime()) {
            return TIME_FORMAT_CONFLICT;
        }

        int comparisonResult = Command.compareTimes(startTime, endTime);
        if (comparisonResult >= 0) {
            return TIME_CONFLICT;
        }
        return null;
    }

    /**
     * Validates time format string.
     *
     * @param timeString The time string to validate.
     * @return Error message if validation fails, null if valid.
     */
    public static String checkTimeFormat(String timeString) {
        Time time = new Time(timeString);
        if (!time.isValid()) {
            return time.getErrorMessage();
        }
        return null;
    }

    /**
     * Handles unknown commands by displaying an expected input message.
     */
    public static void handleUnknownCommand() {
        System.out.println(UNKNOWN_COMMAND);
    }
}