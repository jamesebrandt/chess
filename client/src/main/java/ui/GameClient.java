package ui;
import Exceptions.ResponseException;

import java.util.Arrays;

public class GameClient{

    private PrintBoard whiteBoard;
    private PrintBoard blackBoard;

    private final ServerFacade serverfacade;
    private final WebSocketFacade webSocketFacade;

    public GameClient(String serverUrl, ServerMessageObserver serverMessageObserver) throws ResponseException {
        this.serverfacade = ServerFacade.getInstance(serverUrl);
        this.webSocketFacade = new WebSocketFacade(serverUrl, serverMessageObserver);
    }

    public String eval(String inputLine) {

        whiteBoard = new PrintBoard(true);
        blackBoard = new PrintBoard(false);

        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "EXIT" -> "Leaving Game";
                case "MOVE" -> move(params);
                case "DRAW" -> drawBoard();
                default -> help();

            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String move(String... input) throws ResponseException {
        webSocketFacade.makeMove(input[0]);
        return "Move made to " + input[1];
    }

    //save the board as we go so if the server closes or the game is over then we keep the board

    public String drawBoard(String... input){
        whiteBoard.drawBoard();
        System.out.println();
        blackBoard.drawBoard();

        return "Both Boards Drawn";
    }

    public String help() {
        return """
                - Draw
                - Move
                - Exit
                - Help
                """;
    }
}
