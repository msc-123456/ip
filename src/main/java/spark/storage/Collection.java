package spark.storage;

import spark.task.Task;
import java.util.ArrayList;

/**
 * Manage the collection of tasks and provides operations for tasks,
 * including adding tasks, getting tasks, deleting tasks,
 * marking tasks and getting the number of tasks.
 */
public class Collection {
    private static ArrayList<Task> tasks = Storage.loadTasks();

    /**
     * Add a new task and save tasks.
     *
     * @param task The task to be added.
     */
    public static void addTask(Task task) {
        tasks.add(task);
        Storage.saveTasks(tasks);
    }

    /**
     * Get a task by index.
     *
     * @param index The index of the task.
     * @return The task at the index.
     */
    public static Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Delete a task by index and save tasks.
     *
     * @param index The of the task to remove.
     * @return The removed task.
     */
    public static Task removeTask(int index) {
        Task removedTask = tasks.remove(index);
        Storage.saveTasks(tasks);
        return removedTask;
    }

    /**
     * Marks a task as done or undone.
     *
     * @param index The index of the task.
     * @param isDone True to mark as done, false to unmark.
     */
    public static void markTask(int index, boolean isDone) {
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.unmark();
        }
        Storage.saveTasks(tasks);
    }

    /**
     * Gets the total number of the tasks.
     *
     * @return The number of tasks.
     */
    public static int getTaskCount() {
        return tasks.size();
    }
}