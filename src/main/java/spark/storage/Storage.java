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

    private static final String BY = "By: ";
    private static final String FROM = "From: ";
    private static final String TO = "To: ";

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
                    line = "T | " + (task.getStatusIcon().equals("X") ? "1" : "0") + " | " + task.getDescription();
                } else if (task instanceof Deadline) {
                    Deadline d = (Deadline) task;
                    if (!d.getBy().isValid()) {
                        System.out.println("Warning: Skipping deadline with invalid time: " + d.getDescription());
                        continue;
                    }
                    String timeStr = d.getBy().toStorageString();
                    line = "D | " + (d.getStatusIcon().equals("X") ? "1" : "0") + " | " + d.getDescription() + " | " + timeStr;
                } else if (task instanceof Event) {
                    Event e = (Event) task;
                    if (!e.getFrom().isValid() || !e.getTo().isValid()) {
                        System.out.println("Warning: Skipping event with invalid time: " + e.getDescription());
                        continue;
                    }
                    String fromStr = e.getFrom().toStorageString();
                    String toStr = e.getTo().toStorageString();
                    line = "E | " + (e.getStatusIcon().equals("X") ? "1" : "0") + " | " + e.getDescription() + " | " + fromStr + " | " + toStr;
                }
                writer.write(line + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
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
                String[] parts = line.split(" \\| ");

                if (parts.length < TODO_ELEMENT_NUM) {
                    hasInvaildTask = true;
                    System.out.println("Error line: " + lineNumber);
                    System.out.println(ERROR_UNKNOWN + line);
                    continue;
                }

                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("1");
                String description = parts[2].trim();

                Task task = null;
                switch (type) {
                    case "T":
                        if (parts.length != TODO_ELEMENT_NUM) {
                            hasInvaildTask = true;
                            System.out.println("Error line: " + lineNumber);
                            System.out.println(ERROR_TODO + line);
                            continue;
                        }
                        task = new Todo(description);
                        break;
                    case "D":
                        if (parts.length != DEADLINE_ELEMENT_NUM) {
                            hasInvaildTask = true;
                            System.out.println("Error line: " + lineNumber);
                            System.out.println(ERROR_DEADLINE + line);
                            continue;
                        }

                        String deadlineTime = parts[3].trim();
                        if (!isValidTimeFormat(deadlineTime)) {
                            hasInvaildTask = true;
                            System.out.println("Error line: " + lineNumber);
                            System.out.println(ERROR_DEADLINE + line);
                            continue;
                        }
                        task = new Deadline(description, deadlineTime);
                        break;
                    case "E":
                        if (parts.length != EVENT_ELEMENT_NUM) {
                            hasInvaildTask = true;
                            System.out.println("Error line: " + lineNumber);
                            System.out.println(ERROR_EVENT + line);
                            continue;
                        }

                        String fromTime = parts[3].trim();
                        String toTime = parts[4].trim();
                        if (!isValidTimeFormat(fromTime) || !isValidTimeFormat(toTime)) {
                            hasInvaildTask = true;
                            System.out.println("Error line: " + lineNumber);
                            System.out.println(ERROR_EVENT + line);
                            continue;
                        }
                        task = new Event(description, fromTime, toTime);
                        break;
                    default:
                        System.out.println(ERROR_UNKNOWN + line);
                        continue;
                }

                if (isDone) {
                    task.markAsDone();
                }
                tasks.add(task);
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
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
