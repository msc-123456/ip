package spark.ui;

import spark.process_input.Command;
import spark.storage.Storage;

public class Spark {
    public static void main(String[] args) {
        Storage.loadTasks();
        showWelcomeMessage();

        boolean isRunning = true;
        while (isRunning) {
            String input = Command.getInput();
            isRunning = Command.executeCommand(input);
        }
    }

    private static void showWelcomeMessage() {
        String logo = "  ███████╗██████╗  █████╗ ██████╗ ██╗  ██╗\n" +
                      "  ██╔════╝██╔══██╗██╔══██╗██╔══██╗██║ ██╔╝\n" +
                      "  ███████╗██████╔╝███████║██████╔╝█████╔╝ \n" +
                      "  ╚════██║██╔═══╝ ██╔══██║██╔══██╗██╔═██╗ \n" +
                      "  ███████║██║     ██║  ██║██║  ██║██║  ██╗\n" +
                      "  ╚══════╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝\n";

        Command.printLine();
        System.out.println(logo + "\n" + "Hello! I'm Spark!\nCan I help you?");
        Command.printLine();
    }
}
