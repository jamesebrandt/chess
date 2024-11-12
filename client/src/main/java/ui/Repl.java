package ui;

import java.util.Scanner;
import java.util.stream.StreamSupport;

public class Repl{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;

    public Repl(String serverUrl){
        preLoginClient = new PreLoginClient();
        postLoginClient = new PostLoginClient();
        gameClient = new GameClient();
    }

    public void run(){
        System.out.println("Welcome to the Chess Server! Sign in to Start");
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        result = result.toUpperCase();

        while (!result.equals("QUIT")){
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = preLoginClient.eval(line);
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
