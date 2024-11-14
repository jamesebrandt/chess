package ui;

import java.util.Scanner;

public class Repl{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private replState gameState;

    public Repl(String serverUrl){
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
        gameClient = new GameClient();
        gameState = replState.PRELOGIN;
    }

    public enum replState{
        PRELOGIN,
        LOGGEDIN,
        INGAME,
        EXITING
    }

    public void run(){
        System.out.println("Welcome to the Chess Server! Sign in to Start");
        if (gameState.equals(replState.PRELOGIN)) {preLogin();}
        if (gameState.equals(replState.LOGGEDIN)) {loggedIn(null);}
        if (gameState.equals(replState.INGAME)) {inGame();}
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

    private void loggedIn(String authToken){

        System.out.println("Logged in!");
        System.out.print(postLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (gameState.equals(replState.LOGGEDIN)){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = postLoginClient.eval(line);
                if (result.equals("playing game")){
                    gameState = replState.INGAME;
                    return;
                }
                else if (result.equals("Quitting Client")){
                    gameState = replState.EXITING;
                    return;
                }else if (result.equals("Quitting Client")) {
                    gameState = replState.EXITING;
                    return;
                }

                else {
                    System.out.print(result);
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
                    gameState.equals(replState.LOGGEDIN);
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
