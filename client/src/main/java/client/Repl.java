package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type help to get started. ♕");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                if (!result.equals("quit")) {
                    System.out.print(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);
                }
            } catch (Throwable e) {
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
            }
        }
        System.out.println("\nGoodbye!");
    }

    private void printPrompt() {
        String state = client.isLoggedIn() ? "[LOGGED_IN]" : "[LOGGED_OUT]";
        System.out.print("\n" + RESET_TEXT_COLOR + state + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}
