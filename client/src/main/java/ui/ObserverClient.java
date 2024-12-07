package ui;

import Exceptions.ResponseException;

import java.util.Arrays;

public class ObserverClient {

    private PrintBoard whiteBoard;
    private PrintBoard blackBoard;

    private final ServerFacade serverfacade;
    private final WebSocketFacade webSocketFacade;

    public ObserverClient(String serverUrl, ServerMessageObserver serverMessageObserver, String auth, int gameId) throws ResponseException {
        this.serverfacade = ServerFacade.getInstance(serverUrl);
        this.webSocketFacade = new WebSocketFacade(serverUrl, serverMessageObserver, auth, gameId);
    }

    public String eval(String inputLine) {

        whiteBoard = new PrintBoard(true);
        blackBoard = new PrintBoard(false);

        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            return switch (cmd) {
                case "EXIT" -> "Leaving Game";
                case "DRAW" -> drawBoard();
                default -> help();

            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String drawBoard(String... input){
        whiteBoard.drawBoard();
        System.out.println();
        blackBoard.drawBoard();

        return "Both Boards Drawn";
    }


    public String help(){
        return """
                - Draw
                - Exit
                - Help
                """;
    }
}
