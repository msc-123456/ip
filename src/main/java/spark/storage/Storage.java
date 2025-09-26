package spark.storage;

import spark.task.Task;
import spark.task.Todo;
import spark.task.Deadline;
import spark.task.Event;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private static final String FILE_PATH = "./data/spark.txt";
    private static final String DIR_PATH = "./data";

    private static boolean hasInvaildTask = false;

    private static final int TODO_ELEMENT_NUM = 3;
    private static final int DEADLINE_ELEMENT_NUM = 4;
    private static final int EVENT_ELEMENT_NUM = 5;

    private static final String ERROR_TODO = "Skipping corrupted todo: ";
    private static final String ERROR_DEADLINE = "Skipping corrupted deadline: ";
    private static final String ERROR_EVENT = "Skipping corrupted event: ";
    private static final String ERROR_UNKNOWN = "Skipping unknown message: ";
    private static final String INVALID_TASK = "Your task file has been updated and incorrect task information has been deleted";

    public static void saveTasks(ArrayList<Task> tasks) {
        try {
            Files.createDirectories(Paths.get(DIR_PATH));

            deleteInvaildTasks();
            FileWriter writer = new FileWriter(FILE_PATH);
            for (Task task : tasks) {
                String line = "";
                if (task instanceof Todo) {
                    line = saveTodo(task);
                } else if (task instanceof Deadline) {
                    line = saveDeadline(task);
                } else if (task instanceof Event) {
                    line = saveEvent(task);
                }
                writer.write(line + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private static String saveTodo(Task task) {
        return "T | " + (task.getStatusIcon().equals("X") ? "1" : "0") + " | " + task.getDescription();
    }

    private static String saveDeadline(Task task) {
        Deadline d = (Deadline) task;
        String timeStr = d.getBy().toStorageString();
        return "D | " + (d.getStatusIcon().equals("X") ? "1" : "0") + " | " + d.getDescription() + " | " + timeStr;
    }

    private static String saveEvent(Task task) {
        Event e = (Event) task;
        String fromStr = e.getFrom().toStorageString();
        String toStr = e.getTo().toStorageString();
        return "E | " + (e.getStatusIcon().equals("X") ? "1" : "0") + " | " + e.getDescription() + " | " + fromStr + " | " + toStr;
    }

    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return tasks;
            }

            Scanner scanner = new Scanner(file);
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                Task task = parseTaskFromLine(line, lineNumber);
                if (task != null) {
                    tasks.add(task);
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    private static Task parseTaskFromLine(String line, int lineNumber) {
        String[] parts = line.split(" \\| ");

        if (parts.length < TODO_ELEMENT_NUM) {
            hasInvaildTask = printErrorMessage(ERROR_UNKNOWN, line, lineNumber);
            return null;
        }

        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        Task task = null;
        switch (type) {
            case "T":
                task = loadTodo(parts, description, line, lineNumber);
                break;
            case "D":
                task = loadDeadline(parts, description, line, lineNumber);
                break;
            case "E":
                task = loadEvent(parts, description, line, lineNumber);
                break;
            default:
                hasInvaildTask = printErrorMessage(ERROR_UNKNOWN, line, lineNumber);
        }

        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static Task loadTodo(String[] parts, String description, String line, int lineNumber) {
        if (parts.length != TODO_ELEMENT_NUM) {
            hasInvaildTask = printErrorMessage(ERROR_TODO, line, lineNumber);
            return null;
        }
        return new Todo(description);
    }

    private static Task loadDeadline(String[] parts, String description, String line, int lineNumber) {
        if (parts.length != DEADLINE_ELEMENT_NUM) {
            hasInvaildTask = printErrorMessage(ERROR_DEADLINE, line, lineNumber);
            return null;
        }

        String deadlineTime = parts[3].trim();
        if (!isValidTimeFormat(deadlineTime)) {
            hasInvaildTask = printErrorMessage(ERROR_DEADLINE, line, lineNumber);
            return null;
        }

        return new Deadline(description, deadlineTime);
    }

    private static Task loadEvent(String[] parts, String description, String line, int lineNumber) {
        if (parts.length != EVENT_ELEMENT_NUM) {
            hasInvaildTask = printErrorMessage(ERROR_EVENT, line, lineNumber);
            return null;
        }

        String fromTime = parts[3].trim();
        String toTime = parts[4].trim();
        if (!isValidTimeFormat(fromTime) || !isValidTimeFormat(toTime)) {
            hasInvaildTask = printErrorMessage(ERROR_EVENT, line, lineNumber);
            return null;
        }

        return new Event(description, fromTime, toTime);
    }

    private static boolean printErrorMessage(String error, String line, int lineNumber) {
        System.out.println("Error line: " + lineNumber);
        System.out.println(error + line);
        return true;
    }

    private static boolean isValidTimeFormat(String timeString) {
        Time tempTime = new Time(timeString);
        return tempTime.isValid();
    }

    private static void deleteInvaildTasks() {
        if (hasInvaildTask) {
            System.out.println(INVALID_TASK);
        }
        hasInvaildTask = false;
    }
}
