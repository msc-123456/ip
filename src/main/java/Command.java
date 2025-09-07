import java.util.Scanner;

public class Command {
    private static final int MAX_TASKS = 100;
    private static Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;
    private static Scanner user = new Scanner(System.in);

    private static final int LEN_TODO = SparkException.LEN_TODO;
    private static final int LEN_DEADLINE = SparkException.LEN_DEADLINE;
    private static final int LEN_EVENT = SparkException.LEN_EVENT;
    private static final int LEN_FROM = SparkException.LEN_FROM;
    private static final int LEN_TO = SparkException.LEN_TO;

    public static boolean executeCommand(String input) {
        String command = input.split(" ")[0].toLowerCase();

        switch (command) {
            case "bye":
                handleByeCommand();
                return false;
            case "list":
                handleListCommand();
                break;
            case "mark":
                handleMarkCommand(input, true);
                break;
            case "unmark":
                handleMarkCommand(input, false);
                break;
            case "todo":
                handleTodoCommand(input);
                break;
            case "deadline":
                handleDeadlineCommand(input);
                break;
            case "event":
                handleEventCommand(input);
                break;
            default:
                SparkException.handleUnknownCommand();
                break;
        }
        return true;
    }

    private static void handleByeCommand() {
        printLine();
        System.out.println("Bye! See you~");
        printLine();
        user.close();
    }

    private static void handleListCommand() {
        printLine();
        System.out.println("This is your task list:");
        if (taskCount == 0) {
            System.out.println("You don't have any tasks yet. Try to create one~");
        }
        for (int i = 0; i < taskCount; i++) {
            System.out.println("    " + (i + 1) + ". " + tasks[i]);
        }
        printLine();
    }

    private static void handleMarkCommand(String input, boolean isMark) {
        SparkException.setTaskCount(taskCount);

        String error = SparkException.checkMarkUnmark(input);
        if (error != null) {
            System.out.println(error);
            return;
        }

        int index = getTaskIndex(input);
        if (isMark) {
            tasks[index].markAsDone();
            printLine();
            System.out.println("    Nice! This task is finished:");
        } else {
            tasks[index].unmark();
            printLine();
            System.out.println("    OK, don't forget to do it:");
        }
        System.out.println("    " + tasks[index]);
        printLine();
    }

    private static void handleTodoCommand(String input) {
        SparkException.setTaskCount(taskCount);

        String error = SparkException.checkTaskListFull();
        if (error != null) {
            System.out.println(error);
            return;
        }

        error = SparkException.checkTodo(input);
        if (error != null) {
            System.out.println(error);
            return;
        }

        String description = input.substring(LEN_TODO).trim();
        addTask(new Todo(description));
    }

    private static void handleDeadlineCommand(String input) {
        SparkException.setTaskCount(taskCount);

        String error = SparkException.checkTaskListFull();
        if (error != null) {
            System.out.println(error);
            return;
        }

        error = SparkException.checkDeadline(input);
        if (error != null) {
            System.out.println(error);
            return;
        }

        String[] parts = input.split("/by", 2);
        String description = parts[0].substring(LEN_DEADLINE).trim();
        String by = parts[1].trim();
        addTask(new Deadline(description, by));
    }

    private static void handleEventCommand(String input) {
        SparkException.setTaskCount(taskCount);

        String error = SparkException.checkTaskListFull();
        if (error != null) {
            System.out.println(error);
            return;
        }

        error = SparkException.checkEvent(input);
        if (error != null) {
            System.out.println(error);
            return;
        }

        int fromIndex = input.indexOf("/from");
        int toIndex = input.indexOf("/to");

        String description = input.substring(LEN_EVENT, fromIndex).trim();
        String from = input.substring(fromIndex + LEN_FROM, toIndex).trim();
        String to = input.substring(toIndex + LEN_TO).trim();
        addTask(new Event(description, from, to));
    }

    private static void addTask(Task task) {
        tasks[taskCount] = task;
        printLine();
        System.out.println("    Got it! New task:");
        System.out.println("    " + task);
        System.out.println("    Now you have " + (taskCount + 1) + " tasks in the list.");
        printLine();
        taskCount++;
        SparkException.setTaskCount(taskCount);
    }

    private static int getTaskIndex(String input) {
        String[] parts = input.split(" ");
        return Integer.parseInt(parts[1]) - 1;
    }

    public static void printLine() {
        System.out.println("___________________________________");
    }

    public static String getInput() {
        return user.nextLine().trim();
    }
}