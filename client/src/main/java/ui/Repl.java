package ui;

import java.util.Scanner;

public class Repl{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private loopState gameState;
    private ServerFacade serverFacade;

    public Repl(String serverUrl){
        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient(serverUrl);
        this.gameClient = new GameClient(serverUrl);
        this.gameState = loopState.PRELOGIN;
        this.serverFacade = ServerFacade.getInstance(serverUrl);
    }

    public enum loopState{
        PRELOGIN,
        LOGGEDIN,
        INGAME,
        EXITING,
        OBSERVING
    }

    public void run(){
        while(!gameState.equals(loopState.EXITING)) {
            if (gameState.equals(loopState.PRELOGIN)) {
                System.out.println("Welcome to the Chess Server! Sign in to Begin");
                preLogin();
            }
            if (gameState.equals(loopState.LOGGEDIN)) {
                loggedIn();
            }
            if (gameState.equals(loopState.INGAME)) {
                inGame();
            }
            if (gameState.equals(loopState.OBSERVING)) {
                observing();
            }
        }
    }

    private void preLogin(){
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!gameState.equals(loopState.EXITING) && !result.equals("Quitting Client")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = preLoginClient.eval(line);
                if (result.equals("Successful Login")){
                    gameState = loopState.LOGGEDIN;
                    return;
                } else if (result.startsWith("User Registered and logged in under ")) {
                    gameState = loopState.LOGGEDIN;
                    return;
                }
                if (!gameState.equals(loopState.EXITING)) {
                    System.out.print(result);
                }
                else{
                    System.out.print("Closing Client");
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void loggedIn(){

        System.out.println("Logged in as " + serverFacade.getCurrentUsername());
        System.out.print(postLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (gameState.equals(loopState.LOGGEDIN)){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = postLoginClient.eval(line);
                String userName = serverFacade.getCurrentUsername();
                switch (result) {
                    case "playing game" -> {
                        gameState = loopState.INGAME;
                        return;
                    }
                    case "Quitting Client" -> {
                        gameState = loopState.EXITING;
                        return;
                    }
                    case "Logged out!" -> {
                        gameState = loopState.PRELOGIN;
                        return;
                    }
                    default -> {
                        if (result.startsWith(userName)) {
                            System.out.printf("Welcome, %s! %s%n", userName, result);
                            gameState = loopState.INGAME;
                            return;
                        } else if (result.startsWith("Observing game:")){
                            System.out.printf("Welcome, %s! %s%n", userName, result);
                            gameState = loopState.OBSERVING;
                            return;
                        }
                        else {
                            System.out.print(result);
                        }
                    }
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void inGame(){

        System.out.println("In Game!");
        System.out.print(gameClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("QUIT") && !result.equals("Exiting the game") && !gameState.equals(loopState.EXITING)){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = gameClient.eval(line);
                if (result.equals("Leaving Game")){
                    System.out.println(result);
                    gameState = loopState.LOGGEDIN;
                    return;
                }else {
                    System.out.print(result);
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void observing(){

        System.out.println("Observing Game");
        System.out.print("""
                - Draw
                - Exit_Game
                - Help
                """);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("QUIT") && !result.equals("Exiting the game") && !gameState.equals(loopState.EXITING)){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = gameClient.eval(line);
                if (result.equals("Leaving Game")){
                    System.out.println(result);
                    gameState = loopState.LOGGEDIN;
                    return;
                }else {
                    System.out.print(result);
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }



    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}
