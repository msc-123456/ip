package spark.process_input;

public class SparkException {
    private static final int MAX_TASKS = 100;
    private static int taskCount = 0;

    public static final int LEN_TODO = "todo".length();
    public static final int LEN_DEADLINE = "deadline".length();
    public static final int LEN_EVENT = "event".length();
    public static final int LEN_FROM = "/from".length();
    public static final int LEN_TO = "/to".length();

    public static void setTaskCount(int count) {
        taskCount = count;
    }

    public static String checkMarkUnmark(String input) {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            return "Please use: " + parts[0] + " <number>";
        }
        try {
            int index = Integer.parseInt(parts[1]) - 1;
            if (index < 0 || index >= taskCount) {
                if (taskCount == 0) {
                    return "You don't have any tasks yet. Try to create one~";
                }else {
                    return "Sorry, I cannot find this task. Please try a number within " + taskCount + "~";
                }
            }
        } catch (NumberFormatException e) {
            return "Please use: " + parts[0] + " <number>";
        }
        return null;
    }

    public static String checkTodo(String input) {
        if (input.length() <= LEN_TODO) {
            return "Oh, I need a name for the todo task~";
        }
        String description = input.substring(LEN_TODO).trim();
        if (description.isEmpty()) {
            return "Oh, I need a name for the todo task~";
        }
        return null;
    }

    public static String checkDeadline(String input) {
        if (!input.contains("/by")) {
            return "Please use: deadline <task name> /by <time>";
        }

        if (input.indexOf("/by") <= LEN_DEADLINE) {
            return "Oh, I need a name for the deadline task~";
        }

        String[] parts = input.split("/by", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return "Hi! When is the deadline for this task?";
        }

        String beforeBy = parts[0].substring(LEN_DEADLINE).trim();
        if (beforeBy.isEmpty()) {
            return "Oh, I need a name for the deadline task~";
        }

        return null;
    }

    public static String checkEvent(String input) {
        if (!input.contains("/from") || !input.contains("/to")) {
            return "Please use: event <task name> /from <time> /to <time>";
        }

        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");
        if (fromIndex > toIndex) {
            return "Please use the correct order: /from before /to!";
        }

        if (fromIndex <= LEN_EVENT) {
            return "Oh, I need a name for the event task~";
        }

        String betweenFromAndTo = input.substring(fromIndex + LEN_FROM, toIndex).trim();
        if (betweenFromAndTo.isEmpty()) {
            return "Hi! When is the start time of this task?\nPlease use: event <task name> /from <time> /to <time>";
        }

        String afterTo = input.substring(toIndex + LEN_TO).trim();
        if (afterTo.isEmpty()) {
            return "Hi! When is the end time of this task?\nPlease use: event <task name> /from <time> /to <time>";
        }

        String beforeFrom = input.substring(LEN_EVENT, fromIndex).trim();
        if (beforeFrom.isEmpty()) {
            return "Oh, I need a name for the event task~\nPlease use: event <task name> /from <time> /to <time>";
        }

        return null;
    }

    public static String checkTaskListFull() {
        if (taskCount >= MAX_TASKS) {
            return "Sorry! Task list is full (max " + MAX_TASKS + " tasks).";
        }
        return null;
    }

    public static void handleUnknownCommand() {
        System.out.println("Sorry, I can't understand your command. ( 0.0 )");
        System.out.println("Please use these command ( ^_^ ):");
        System.out.println("todo, deadline, event, mark, unmark, list, bye");
    }
}