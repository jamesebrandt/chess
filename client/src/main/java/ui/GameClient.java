package ui;

import java.util.Arrays;

public class GameClient {

    private PrintBoard whiteBoard;
    private PrintBoard blackBoard;

    private final ServerFacade serverfacade;

    public GameClient(String serverUrl){
        this.serverfacade = ServerFacade.getInstance(serverUrl);
    }


    public String eval(String inputLine) {

        whiteBoard = new PrintBoard(true);
        blackBoard = new PrintBoard(false);

        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "LOGOUT" -> move(params);
                case "CREATE_GAME" -> createGame(params);
                case "EXIT_GAME" -> "Leaving Game";
                case "DRAW" -> drawBoard();
                default -> help();

            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String move(String... input){
        return "not implemented";
    }

    public String createGame(String... input){
        return "not implemented";
    }

    public String drawBoard(String... input){
        whiteBoard.drawBoard();
        System.out.println();
        blackBoard.drawBoard();

        return "Both Boards Drawn";
    }

    public String help() {
        return """
                - Draw
                - Make_Move
                - Exit_Game
                - Help
                """;
    }
}
