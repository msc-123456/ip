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

    private static final String TASK_LIST = "This is your task list:";
    private static final String EMPTY_TASK_LIST = "You don't have any tasks yet. Try to create one~";

    private static final String MARKED_TASK = "    Nice! This task is finished:";
    private static final String UNMARKED_TASK = "    OK, don't forget to do it:";
    private static final String DELETED_TASK = "    OK. I've removed this task:";

    private static final String FIND_DATE_FORMAT = "Please use: finddate <date>";
    private static final String INVALID_DATE = "Invalid date format. Please use: yyyy-MM-dd";
    private static final String NO_MATCHING_TASKS = "No tasks found for this ";

    private static final String SORTED_EVENTS = "=== EVENTS (sorted by start time) ===";
    private static final String SORTED_DEADLINES = "=== DEADLINE (sorted by due time) ===";
    private static final String SORTED_TODOS = "=== TODOS (no time information) ===";

    private static final String FIND_FORMAT = "Please use: find <keyword>";
    private static final String TASKS_FOR_KEYWORD = "Here are the tasks containing the keyword ";

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
        System.out.println(TASK_LIST);
        if (taskCount == 0) {
            System.out.println(EMPTY_TASK_LIST);
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
            System.out.println(MARKED_TASK);
        } else {
            System.out.println(UNMARKED_TASK);
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
        System.out.println(DELETED_TASK);
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
            System.out.println(FIND_DATE_FORMAT);
            return;
        }

        String dateString = parts[1].trim();
        LocalDate targetDate;
        DateTimeFormatter dateFormatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            targetDate = LocalDate.parse(dateString, dateFormatters);
        } catch (Exception e) {
            System.out.println(INVALID_DATE);
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
        printMatchingTasks(matchingTasks, "date");
    }

    private static void printMatchingTasks(ArrayList<Task> matchingTasks, String matchingType) {
        if (matchingTasks.isEmpty()) {
            System.out.println(NO_MATCHING_TASKS + matchingType);
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
        System.out.println(SORTED_EVENTS);
        for (Event event : events) {
            System.out.println("    " + counter + ". " + event);
            counter++;
        }
        System.out.println();
    }

    private static void printDeadlines(ArrayList<Deadline> deadlines) {
        int counter = 1;
        System.out.println(SORTED_DEADLINES);
        for (Deadline deadline : deadlines) {
            System.out.println("    " + counter + ". " + deadline);
            counter++;
        }
        System.out.println();
    }

    private static void printTo(ArrayList<Todo> todos) {
        int counter = 1;
        System.out.println(SORTED_TODOS);
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
            System.out.println(FIND_FORMAT);
            return;
        }

        String keyword = parts[1].trim().toLowerCase();
        if (keyword.isEmpty()) {
            System.out.println(FIND_FORMAT);
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
        System.out.println(TASKS_FOR_KEYWORD + keyword + " :");
        printMatchingTasks(matchingTasks, "keyword");
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