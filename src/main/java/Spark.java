public class Spark {
    public static void main(String[] args) {
        showWelcomeMessage();

        boolean isRunning = true;
        while (isRunning) {
            String input = Command.getInput();
            isRunning = Command.executeCommand(input);
        }
    }

    private static void showWelcomeMessage() {
        Command.printLine();
        System.out.println("  ███████╗██████╗  █████╗ ██████╗ ██╗  ██╗");
        System.out.println("  ██╔════╝██╔══██╗██╔══██╗██╔══██╗██║ ██╔╝");
        System.out.println("  ███████╗██████╔╝███████║██████╔╝█████╔╝ ");
        System.out.println("  ╚════██║██╔═══╝ ██╔══██║██╔══██╗██╔═██╗ ");
        System.out.println("  ███████║██║     ██║  ██║██║  ██║██║  ██╗");
        System.out.println("  ╚══════╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝\n");
        System.out.println("Hello! I'm Spark!\nCan I help you?");
        Command.printLine();
    }
}
