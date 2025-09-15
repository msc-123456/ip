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

    public static void saveTasks(ArrayList<Task> tasks) {
        try {
            // 创建目录（如果不存在）
            Files.createDirectories(Paths.get(DIR_PATH));

            FileWriter writer = new FileWriter(FILE_PATH);
            for (Task task : tasks) {
                String line = "";
                if (task instanceof Todo) {
                    line = "T | " + (task.getStatusIcon().equals("X") ? "1" : "0") + " | " + task.getDescription();
                } else if (task instanceof Deadline) {
                    Deadline d = (Deadline) task;
                    line = "D | " + (d.getStatusIcon().equals("X") ? "1" : "0") + " | " + d.getDescription() + " | " + d.getBy();
                } else if (task instanceof Event) {
                    Event e = (Event) task;
                    line = "E | " + (e.getStatusIcon().equals("X") ? "1" : "0") + " | " + e.getDescription() + " | " + e.getFrom() + " | " + e.getTo();
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
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" \\| ");

                if (parts.length < 3) {
                    System.out.println("Skipping corrupted line: " + line);
                    continue;
                }

                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("1");
                String description = parts[2].trim();

                Task task = null;
                switch (type) {
                    case "T":
                        task = new Todo(description);
                        break;
                    case "D":
                        if (parts.length < 4) {
                            System.out.println("Skipping corrupted deadline: " + line);
                            continue;
                        }
                        task = new Deadline(description, parts[3].trim());
                        break;
                    case "E":
                        if (parts.length < 5) {
                            System.out.println("Skipping corrupted event: " + line);
                            continue;
                        }
                        task = new Event(description, parts[3].trim(), parts[4].trim());
                        break;
                    default:
                        System.out.println("Skipping unknown task type: " + line);
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
}
