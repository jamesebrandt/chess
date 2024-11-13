package ui;

import java.util.Scanner;

public class Repl{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private PrintBoard whiteBoard;
    private PrintBoard blackBoard;

    public Repl(String serverUrl){
        preLoginClient = new PreLoginClient();
        postLoginClient = new PostLoginClient();
        gameClient = new GameClient();
        whiteBoard = new PrintBoard(true);
        blackBoard = new PrintBoard(false);
    }

    public void run(){
        System.out.println("Welcome to the Chess Server! Sign in to Start");
        System.out.print(preLoginClient.help());

        whiteBoard.printBoard();
        blackBoard.printBoard();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("QUIT")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = preLoginClient.eval(line);
                if (result.equals("Successful Login")){
                    loggedIn();
                }
                System.out.print(result);
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

        while (!result.equals("QUIT") && !result.equals("LOGOUT")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = postLoginClient.eval(line);
                if (result.equals("playing game")){
                    inGame();
                }
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void inGame(){

        System.out.println("Logged in!");
        System.out.print(postLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("QUIT") && !result.equals("EXIT_GAME")){
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = postLoginClient.eval(line);
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }
}
