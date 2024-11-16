package ui;

import java.util.Scanner;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private LoopState gameState;
    private ServerFacade serverFacade;

    public Repl(String serverUrl) {
        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient(serverUrl);
        this.gameClient = new GameClient(serverUrl);
        this.gameState = LoopState.PRELOGIN;
        this.serverFacade = ServerFacade.getInstance(serverUrl);
    }

    public enum LoopState {
        PRELOGIN,
        LOGGEDIN,
        INGAME,
        EXITING,
        OBSERVING
    }

    public void run() {
        while (!gameState.equals(LoopState.EXITING)) {
            if (gameState.equals(LoopState.PRELOGIN)) {
                preLogin();
            } else if (gameState.equals(LoopState.LOGGEDIN)) {
                loggedIn();
            } else if (gameState.equals(LoopState.INGAME)) {
                inGame();
            } else if (gameState.equals(LoopState.OBSERVING)) {
                observing();
            }
        }
    }

    private void preLogin() {
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!gameState.equals(LoopState.EXITING) && !result.equals("Quitting Client")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = preLoginClient.eval(line);
                if (result.equals("Successful Login") || result.startsWith("User Registered and logged in under ")) {
                    gameState = LoopState.LOGGEDIN;
                    return;
                }
                if (!gameState.equals(LoopState.EXITING) && !result.equals("Quitting Client")) {
                    System.out.print(result);
                } else {
                    gameState = LoopState.EXITING;
                    System.out.print("Closing Client");
                }
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        System.out.println();
    }

    private void loggedIn() {
        System.out.println("Logged in as " + serverFacade.getCurrentUsername());
        System.out.print(postLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (gameState.equals(LoopState.LOGGEDIN)) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = postLoginClient.eval(line);
                String userName = serverFacade.getCurrentUsername();

                if (result.equals("playing game")) {
                    gameState = LoopState.INGAME;
                    return;
                } else if (result.equals("Quitting Client")) {
                    gameState = LoopState.EXITING;
                    return;
                } else if (result.equals("Logged out!")) {
                    gameState = LoopState.PRELOGIN;
                    return;
                }

                if (result.startsWith(userName)) {
                    System.out.printf("Welcome, %s! %s%n", userName, result);
                    gameState = LoopState.INGAME;
                    return;
                } else if (result.startsWith("Observing game:")) {
                    System.out.printf("Welcome, %s! %s%n", userName, result);
                    gameState = LoopState.OBSERVING;
                    return;
                } else {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
        System.out.println();
    }

    private void inGame() {
        System.out.println("In Game!");
        System.out.print(gameClient.help());
        observingAndInGame();
    }

    private void observing() {
        System.out.println("Observing Game");
        System.out.print("""
                - Draw
                - Exit_Game
                - Help
                """);

        observingAndInGame();
    }

    private void observingAndInGame() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("QUIT") && !result.equals("Exiting the game") && !gameState.equals(LoopState.EXITING)) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = gameClient.eval(line);
                if (result.equals("Leaving Game")) {
                    System.out.println(result);
                    gameState = LoopState.LOGGEDIN;
                    return;
                } else {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }
}
