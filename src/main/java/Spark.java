import java.util.Scanner;

public class Spark {
    private static void printLine() {
        System.out.println("__________________________________________________");
    }

    public static void main(String[] args) {
        Scanner user = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;

        printLine();
        System.out.println("  ███████╗██████╗  █████╗ ██████╗ ██╗  ██╗");
        System.out.println("  ██╔════╝██╔══██╗██╔══██╗██╔══██╗██║ ██╔╝");
        System.out.println("  ███████╗██████╔╝███████║██████╔╝█████╔╝ ");
        System.out.println("  ╚════██║██╔═══╝ ██╔══██║██╔══██╗██╔═██╗ ");
        System.out.println("  ███████║██║     ██║  ██║██║  ██║██║  ██╗");
        System.out.println("  ╚══════╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝\n");
        System.out.println("Hello! I'm Spark!\nCan I help you?");
        printLine();

        while (true) {
            String input = user.nextLine().trim();

            if (input.equalsIgnoreCase("bye")) {
                printLine();
                System.out.println("Bye! See you~");
                printLine();
                break;
            } else if (input.equalsIgnoreCase("list")) {
                printLine();
                System.out.println("This is your task list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("    " + (i + 1) + ". " + tasks[i]);
                }
                printLine();
            } else if (input.startsWith("mark")) {
                try {
                    String[] parts = input.split(" ");
                    int index = Integer.parseInt(parts[1]) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsDone();
                        printLine();
                        System.out.println("    Good! This task is done:");
                        System.out.println("    " + tasks[index]);
                        printLine();
                    } else {
                        System.out.println("Invalid task number.");
                    }
                } catch (Exception e) {
                    System.out.println("Please use: mark <number>");
                }
            } else if (input.startsWith("unmark")) {
                try {
                    String[] parts = input.split(" ");
                    int index = Integer.parseInt(parts[1]) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].unmark();
                        printLine();
                        System.out.println("    OK, don't forget to finish it:");
                        System.out.println("    " + tasks[index]);
                        printLine();
                    } else {
                        System.out.println("Invalid task number.");
                    }
                } catch (Exception e) {
                    System.out.println("Please use: unmark <number>");
                }
            } else{
                if (taskCount < 100) {
                    tasks[taskCount] = new Task(input);
                    printLine();
                    System.out.println("    added: " + input);
                    printLine();
                    taskCount++;
                } else {
                    System.out.println("Task list is full (max 100 tasks).");
                }
            }
        }

        user.close();
    }
}
