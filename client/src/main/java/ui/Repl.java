package ui;

import Exceptions.ResponseException;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl implements ServerMessageObserver{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private final ObserverClient observerClient;
    private LoopState gameState;
    private ServerFacade serverFacade;
    private int currentGameId;
    private SessionManager manager;

    public Repl(String serverUrl) throws ResponseException {

        this.serverFacade = ServerFacade.getInstance(serverUrl); // Singleton initialization
        this.manager = new SessionManager();

        String sessionToken = manager.getSessionToken(serverFacade.getCurrentUsername());
        this.currentGameId = 0;

        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient(serverUrl);
        this.gameClient = createGameClient(serverUrl, sessionToken);
        this.observerClient = createObserverClient(serverUrl, sessionToken);

        this.gameState = LoopState.PRELOGIN;
    }

    public enum LoopState {
        PRELOGIN,
        LOGGEDIN,
        INGAME,
        EXITING,
        OBSERVING
    }

    private GameClient createGameClient(String serverUrl, String sessionToken) throws ResponseException {
        return new GameClient(serverUrl, this, sessionToken, currentGameId);
    }

    private ObserverClient createObserverClient(String serverUrl, String sessionToken) throws ResponseException {
        return new ObserverClient(serverUrl, this, sessionToken, currentGameId);
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

                }
                else if(result.startsWith("Move made to ")){
                    System.out.println(result);
                    gameClient.eval("DRAW");

                } else {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
    }

    private void observing() {
        System.out.println("Observing Game");
        System.out.print("""
                - Draw
                - Exit
                - Help
                """);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("QUIT") && !result.equals("Exiting the game") && !gameState.equals(LoopState.EXITING)) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = observerClient.eval(line);
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

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()){
            case NOTIFICATION -> displayNotification(message);
            case LOAD_GAME -> loadGame(message);
            case ERROR -> displayError(message);
        }
    }

    private void displayNotification(ServerMessage message){
        System.out.print("Notification: " + message.toString());
    }

    private void loadGame(ServerMessage message){

    }

    private void displayError(ServerMessage message){
        System.out.print("ERROR: " + message.toString());
    }
}
