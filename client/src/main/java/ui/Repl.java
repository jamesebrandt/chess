package ui;

import java.util.Scanner;

public class Repl{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private replState gameState;
    private ServerFacade serverFacade;

    public Repl(String serverUrl){
        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient(serverUrl);
        this.gameClient = new GameClient(serverUrl);
        this.gameState = replState.PRELOGIN;
        this.serverFacade = ServerFacade.getInstance(serverUrl);
    }

    public enum replState{
        PRELOGIN,
        LOGGEDIN,
        INGAME,
        EXITING
    }

    public void run(){
        while(!gameState.equals(replState.EXITING)) {
            if (gameState.equals(replState.PRELOGIN)) {
                System.out.println("Welcome to the Chess Server! Sign in to Begin");
                preLogin();
            }
            if (gameState.equals(replState.LOGGEDIN)) {
                loggedIn();
            }
            if (gameState.equals(replState.INGAME)) {
                inGame();
            }
        }
    }

    private void preLogin(){
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!gameState.equals(replState.EXITING) && !result.equals("Quitting Client")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = preLoginClient.eval(line);
                if (result.equals("Successful Login")){
                    gameState = replState.LOGGEDIN;
                    return;
                } else if (result.startsWith("User Registered and logged in under ")) {
                    gameState = replState.LOGGEDIN;
                    return;
                }
                if (!gameState.equals(replState.EXITING)) {
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

        while (gameState.equals(replState.LOGGEDIN)){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = postLoginClient.eval(line);
                switch (result) {
                    case "playing game" -> {
                        gameState = replState.INGAME;
                        return;
                    }
                    case "Quitting Client" -> {
                        gameState = replState.EXITING;
                        return;
                    }
                    case "Logged out!" -> {
                        gameState = replState.PRELOGIN;
                        return;
                    }
                    default -> System.out.print(result);
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

        while (!result.equals("QUIT") && !result.equals("Exiting the game") && !gameState.equals(replState.EXITING)){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = gameClient.eval(line);
                if (result.equals("Leaving Game")){
                    System.out.println(result);
                    gameState = replState.LOGGEDIN;
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
