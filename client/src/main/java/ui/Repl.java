package ui;

import Exceptions.ResponseException;
import model.Game;
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
    private PrintBoard printBoard;

    public Repl(String serverUrl) {

        this.serverFacade = ServerFacade.getInstance(serverUrl);
        this.manager = SessionManager.getInstance();
        this.printBoard = new PrintBoard(serverFacade.getCurrentUsername());

        this.currentGameId = 0;

        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient(serverUrl);
        this.gameClient = new GameClient(serverUrl);
        this.observerClient = new ObserverClient(serverUrl);

        this.gameState = LoopState.PRELOGIN;
    }

    public enum LoopState {
        PRELOGIN,
        LOGGEDIN,
        INGAME,
        EXITING,
        OBSERVING
    }


    public void run() throws ResponseException {
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

                if (result.startsWith(" You have been added to game")) {
                    gameState = LoopState.INGAME;
                    return;
                } else if (result.equals("Quitting Client")) {
                    gameState = LoopState.EXITING;
                    return;
                } else if (result.startsWith("Logged out!")) {
                    gameState = LoopState.PRELOGIN;
                    System.out.printf(result + "\n");
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

    private void inGame() throws ResponseException {
        gameClient.connectToWebSocket(this, serverFacade.getAuth(), currentGameId);

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
                if (result.startsWith("Left the Game")) {
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

    private void observing() throws ResponseException {

        observerClient.connectToWebSocket(this, serverFacade.getAuth(), currentGameId);

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

    private void displayNotification(ServerMessage message) {
        System.out.println("\n[" + message.getServerMessageType() + "] " + message.getServerMessage());
    }

    private void loadGame(ServerMessage message){
        Game updatedGame = message.getServerMessageGame();
        printBoard.setBoard(updatedGame.game().getBoard());
        printBoard.drawBoard();
    }

    private void displayError(ServerMessage message){
        System.out.print("ERROR: " + message.toString());
    }
}
