package ui;

import java.util.Scanner;

public class Repl{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private boolean exiting = false;


    public Repl(String serverUrl){
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl);
        gameClient = new GameClient();
    }

    public void run(){
        System.out.println("Welcome to the Chess Server! Sign in to Start");
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!exiting && !result.equals("Quitting Client")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = preLoginClient.eval(line);
                if (result.equals("Successful Login")){
                    loggedIn();
                }
                if (!exiting) {
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

        System.out.println("Logged in!");
        System.out.print(postLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("LOGOUT")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = postLoginClient.eval(line);
                if (result.equals("playing game")){
                    inGame();
                    return;
                }
                if (result.equals("Quitting Client")){
                    exiting = true;
                    return;
                }else {
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

        while (!result.equals("QUIT") && !result.equals("Exiting the game") && !exiting){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = gameClient.eval(line);
                if (result.equals("Leaving Game")){
                    System.out.print(result);
                    loggedIn();
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
