package ui;

import java.util.Arrays;


public class GameClient {

    private PrintBoard whiteBoard;
    private PrintBoard blackBoard;

    public String eval(String inputLine) {

        whiteBoard = new PrintBoard(true);
        blackBoard = new PrintBoard(false);

        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "HELP" -> help();
                case "LOGOUT" -> move(params);
                case "CREATE_GAME" -> exit(params);
                case "QUIT" -> "quit";
                default -> drawBoard();

            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String move(String... input){
        return "not implemented";
    }

    public String exit(String... input){
        return "not implemented";
    }

    public String drawBoard(String... input){
        whiteBoard.drawBoard();
        System.out.println();
        blackBoard.drawBoard();

        return "not implemented";
    }


    public String help() {
        return """
                - Make_Move
                - Exit_Game
                - Help
                - Quit
                """;
    }
}
