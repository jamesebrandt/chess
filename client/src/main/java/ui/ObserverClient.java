package ui;

import Exceptions.ResponseException;

import java.util.Arrays;

public class ObserverClient {

    private PrintBoard board;

    private final ServerFacade serverfacade;
    private WebSocketFacade webSocketFacade;
    private final String serverUrl;

    public ObserverClient(String serverUrl) {
        this.serverfacade = ServerFacade.getInstance(serverUrl);
        this.serverUrl = serverUrl;
    }

    public void connectToWebSocket(ServerMessageObserver serverMessageObserver, String auth, int gameId) throws ResponseException {
        this.webSocketFacade = new WebSocketFacade(serverUrl, serverMessageObserver, auth, gameId);
    }

    public String eval(String inputLine) {

        board = new PrintBoard(serverfacade.getCurrentUsername());

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
        board.PrintBoardForObserver();
        System.out.println();

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
