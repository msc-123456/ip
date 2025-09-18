package spark.storage;

import spark.task.Task;
import java.util.ArrayList;

public class Collection {
    private static ArrayList<Task> tasks = Storage.loadTasks();

    public static void addTask(Task task) {
        tasks.add(task);
        Storage.saveTasks(tasks);
    }

    public static Task getTask(int index) {
        return tasks.get(index);
    }

    public static Task removeTask(int index) {
        Task removedTask = tasks.remove(index);
        Storage.saveTasks(tasks);
        return removedTask;
    }

    public static void markTask(int index, boolean isDone) {
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.unmark();
        }
        Storage.saveTasks(tasks);
    }

    public static int getTaskCount() {
        return tasks.size();
    }
}