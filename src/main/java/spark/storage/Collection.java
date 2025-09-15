package spark.storage;

import spark.task.Task;
import java.util.ArrayList;

public class Collection {
    private static ArrayList<Task> tasks = new ArrayList<>();

    public static void addTask(Task task) {
        tasks.add(task);
    }

    public static Task getTask(int index) {
        return tasks.get(index);
    }

    public static Task removeTask(int index) {
        return tasks.remove(index);
    }

    public static void markTask(int index, boolean isDone) {
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.unmark();
        }
    }

    public static int getTaskCount() {
        return tasks.size();
    }
}