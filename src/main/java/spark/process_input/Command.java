package spark.process_input;

import spark.task.Deadline;
import spark.task.Event;
import spark.task.Task;
import spark.task.Todo;
import spark.storage.Collection;
import spark.storage.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import java.util.Scanner;

public class Command {
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
            case "delete":
                handleDeleteCommand(input);
                break;
            case "schedule":
                handleScheduleCommand();
                break;
            case "finddate":
                handleFindDateCommand(input);
                break;
            case "find":
                handleFindCommand(input);
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
        int taskCount = Collection.getTaskCount();
        printLine();
        System.out.println("This is your task list:");
        if (taskCount == 0) {
            System.out.println("You don't have any tasks yet. Try to create one~");
        }
        for (int i = 0; i < taskCount; i++) {
            System.out.println("    " + (i + 1) + ". " + Collection.getTask(i));
        }
        printLine();
    }

    private static void handleMarkCommand(String input, boolean isMark) {
        String error = SparkException.checkMarkUnmark(input);
        if (error != null) {
            System.out.println(error);
            return;
        }

        int index = getTaskIndex(input);
        Collection.markTask(index, isMark);
        Task task = Collection.getTask(index);

        printLine();
        if (isMark) {
            System.out.println("    Nice! This task is finished:");
        } else {
            System.out.println("    OK, don't forget to do it:");
        }
        System.out.println("    " + task);
        printLine();
    }

    private static void handleDeleteCommand(String input) {
        String error = SparkException.checkDelete(input);
        if (error != null) {
            System.out.println(error);
            return;
        }

        int index = getTaskIndex(input);
        Task task = Collection.removeTask(index);
        int taskCount = Collection.getTaskCount();

        printLine();
        System.out.println("    OK. I've removed this task:");
        System.out.println("    " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
        printLine();
    }

    private static void handleTodoCommand(String input) {
        String error = SparkException.checkTodo(input);
        if (error != null) {
            System.out.println(error);
            return;
        }

        String description = input.substring(LEN_TODO).trim();
        addTask(new Todo(description));
    }

    private static void handleDeadlineCommand(String input) {
        String error = SparkException.checkDeadline(input);
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
        String error = SparkException.checkEvent(input);
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
        Collection.addTask(task);
        int taskCount = Collection.getTaskCount();
        printLine();
        System.out.println("    Got it! New task:");
        System.out.println("    " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
        printLine();
    }

    private static void handleFindDateCommand(String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Please use: finddate <date>");
            return;
        }

        String dateString = parts[1].trim();
        LocalDate targetDate;

        DateTimeFormatter dateFormatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            targetDate = LocalDate.parse(dateString, dateFormatters);
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use: yyyy-MM-dd");
            return;
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();
        int taskCount = Collection.getTaskCount();

        for (int i = 0; i < taskCount; i++) {
            Task task = Collection.getTask(i);
            if (isTaskOnDate(task, targetDate)) {
                matchingTasks.add(task);
            }
        }

        printLine();
        System.out.println("Tasks on " + targetDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy",  Locale.ENGLISH)) + ":");
        if (matchingTasks.isEmpty()) {
            System.out.println("No tasks found for this date.");
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println("    " + (i + 1) + ". " + matchingTasks.get(i));
            }
        }
        printLine();
    }

    private static void handleScheduleCommand() {
        ArrayList<Task> allTasks = getAllTasks();

        if (allTasks.isEmpty()) {
            printLine();
            System.out.println("No tasks found.");
            printLine();
            return;
        }

        ArrayList<Event> events = new ArrayList<>();
        ArrayList<Deadline> deadlines = new ArrayList<>();
        ArrayList<Todo> todos = new ArrayList<>();

        for (Task task : allTasks) {
            if (task instanceof Event) {
                events.add((Event) task);
            } else if (task instanceof Deadline) {
                deadlines.add((Deadline) task);
            } else if (task instanceof Todo) {
                todos.add((Todo) task);
            }
        }

        events.sort((e1, e2) -> compareTimes(e1.getFrom(), e2.getFrom()));
        deadlines.sort((d1, d2) -> compareTimes(d1.getBy(), d2.getBy()));

        printSchedule(events, deadlines, todos);
    }

    public static int compareTimes(Time time1, Time time2) {
        if (time1 == null || !time1.isValid()) return 1;
        if (time2 == null || !time2.isValid()) return -1;

        LocalDate date1 = time1.getDate();
        LocalDate date2 = time2.getDate();

        int dateComparison = date1.compareTo(date2);
        if (dateComparison != 0) {
            return dateComparison;
        }

        if (!time1.hasTime() && time2.hasTime()) {
            return -1;
        }
        if (time1.hasTime() && !time2.hasTime()) {
            return 1;
        }
        if (!time1.hasTime() && !time2.hasTime()) {
            return 0;
        }

        return time1.getDateTime().compareTo(time2.getDateTime());
    }

    private static ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        int taskCount = Collection.getTaskCount();

        for (int i = 0; i < taskCount; i++) {
            allTasks.add(Collection.getTask(i));
        }

        return allTasks;
    }

    private static void printSchedule(ArrayList<Event> events, ArrayList<Deadline> deadlines, ArrayList<Todo> todos) {
        printLine();
        System.out.println("Tasks sorted by time:");
        System.out.println();

        if (!events.isEmpty()) {
            printEvents(events);
        }

        if (!deadlines.isEmpty()) {
            printDeadlines(deadlines);
        }

        if (!todos.isEmpty()) {
            printTo(todos);
        }

        printLine();
    }

    private static void printEvents(ArrayList<Event> events) {
        int counter = 1;
        System.out.println("=== EVENTS (sorted by start time) ===");
        for (Event event : events) {
            System.out.println("    " + counter + ". " + event);
            counter++;
        }
        System.out.println();
    }

    private static void printDeadlines(ArrayList<Deadline> deadlines) {
        int counter = 1;
        System.out.println("=== DEADLINES (sorted by due time) ===");
        for (Deadline deadline : deadlines) {
            System.out.println("    " + counter + ". " + deadline);
            counter++;
        }
        System.out.println();
    }

    private static void printTo(ArrayList<Todo> todos) {
        int counter = 1;
        System.out.println("=== TODOS (no time information) ===");
        for (Todo todo : todos) {
            System.out.println("    " + counter + ". " + todo);
            counter++;
        }
    }

    private static boolean isTaskOnDate(Task task, LocalDate date) {
        if (task instanceof Event) {
            Event event = (Event) task;
            return !event.getFrom().getDate().isAfter(date) && !event.getTo().getDate().isBefore(date);
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return deadline.getBy().isSameDate(date);
        } else {
            return false;
        }
    }

    private static void handleFindCommand(String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Please use: find <keyword>");
            return;
        }

        String keyword = parts[1].trim().toLowerCase();
        if (keyword.isEmpty()) {
            System.out.println("Please provide a keyword to search for.");
            return;
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();
        int taskCount = Collection.getTaskCount();

        for (int i = 0; i < taskCount; i++) {
            Task task = Collection.getTask(i);
            if (task.getDescription().toLowerCase().contains(keyword)) {
                matchingTasks.add(task);
            }
        }

        printLine();
        System.out.println("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            System.out.println("No tasks found containing: " + keyword);
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println("    " + (i + 1) + "." + matchingTasks.get(i));
            }
        }
        printLine();
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