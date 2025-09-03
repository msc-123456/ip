import java.util.Scanner;

public class Spark {
    private static final int MAX_TASKS = 100;
    private static Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;
    private static Scanner user = new Scanner(System.in);

    public static void main(String[] args) {
        showWelcomeMessage();

        boolean isRunning = true;
        while (isRunning) {
            String input = getUserInput();
            isRunning = processInput(input);
        }

        user.close();
    }

    private static void showWelcomeMessage() {
        printLine();
        System.out.println("  ███████╗██████╗  █████╗ ██████╗ ██╗  ██╗");
        System.out.println("  ██╔════╝██╔══██╗██╔══██╗██╔══██╗██║ ██╔╝");
        System.out.println("  ███████╗██████╔╝███████║██████╔╝█████╔╝ ");
        System.out.println("  ╚════██║██╔═══╝ ██╔══██║██╔══██╗██╔═██╗ ");
        System.out.println("  ███████║██║     ██║  ██║██║  ██║██║  ██╗");
        System.out.println("  ╚══════╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝\n");
        System.out.println("Hello! I'm Spark!\nCan I help you?");
        printLine();
    }

    private static String getUserInput() {
        return user.nextLine().trim();
    }

    private static boolean processInput(String input) {
        if (input.equalsIgnoreCase("bye")) {
            handleByeCommand();
            return false;
        } else if (input.equalsIgnoreCase("list")) {
            handleListCommand();
        } else if (input.startsWith("mark")) {
            handleMarkCommand(input, true);
        } else if (input.startsWith("unmark")) {
            handleMarkCommand(input, false);
        } else if (input.startsWith("todo")) {
            handleTodoCommand(input);
        } else if (input.startsWith("deadline")) {
            handleDeadlineCommand(input);
        } else if (input.startsWith("event")) {
            handleEventCommand(input);
        } else {
            handleUnknownCommand();
        }
        return true;
    }

    private static void handleByeCommand() {
        printLine();
        System.out.println("Bye! See you~");
        printLine();
    }

    private static void handleListCommand() {
        printLine();
        System.out.println("This is your task list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println("    " + (i + 1) + ". " + tasks[i]);
        }
        printLine();
    }

    private static void handleMarkCommand(String input, boolean isMark) {
        try {
            int index = getTaskIndex(input);
            if (isValidIndex(index)) {
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
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (Exception e) {
            System.out.println("Please use: " + (isMark ? "mark" : "unmark") + " <number>");
        }
    }

    private static void handleTodoCommand(String input) {
        if (isTaskListFull()) {
            System.out.println("Task list is full (max " + MAX_TASKS + " tasks).");
            return;
        }

        String description = input.substring(4).trim();
        if (description.isEmpty()) {
            System.out.println("Please provide a description for the todo.");
            return;
        }

        addTask(new Todo(description));
    }

    private static void handleDeadlineCommand(String input) {
        if (isTaskListFull()) {
            System.out.println("Task list is full (max " + MAX_TASKS + " tasks).");
            return;
        }

        String rest = input.substring(8).trim();
        String[] parts = rest.split("/by");
        if (parts.length < 2) {
            System.out.println("Please use the format: deadline <description> /by <time>");
            return;
        }

        String description = parts[0].trim();
        String by = parts[1].trim();
        addTask(new Deadline(description, by));
    }

    private static void handleEventCommand(String input) {
        if (isTaskListFull()) {
            System.out.println("Task list is full (max " + MAX_TASKS + " tasks).");
            return;
        }

        String rest = input.substring(5).trim();
        String[] parts = rest.split("/from|/to");
        if (parts.length < 3) {
            System.out.println("Please use the format: event <description> /from <time> /to <time>");
            return;
        }

        String description = parts[0].trim();
        String from = parts[1].trim();
        String to = parts[2].trim();
        addTask(new Event(description, from, to));
    }

    private static void handleUnknownCommand() {
        System.out.println("Unknown command. Please use: todo, deadline, event, list, mark, unmark, or bye.");
    }

    private static void addTask(Task task) {
        tasks[taskCount] = task;
        printLine();
        System.out.println("    Got it! New task:");
        System.out.println("    " + task);
        System.out.println("    Now you have " + (taskCount + 1) + " tasks in the list.");
        printLine();
        taskCount++;
    }

    private static int getTaskIndex(String input) {
        String[] parts = input.split(" ");
        return Integer.parseInt(parts[1]) - 1;
    }

    private static boolean isValidIndex(int index) {
        return index >= 0 && index < taskCount;
    }

    private static boolean isTaskListFull() {
        return taskCount >= MAX_TASKS;
    }

    private static void printLine() {
        System.out.println("___________________________________");
    }
}