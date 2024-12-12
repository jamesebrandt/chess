package ui;
import Exceptions.ResponseException;
import chess.ChessBoard;

import java.util.Arrays;
import java.util.Scanner;

public class GameClient{

    private PrintBoard board;

    private final ServerFacade serverfacade;
    private WebSocketFacade webSocketFacade;
    private final String serverUrl;
    private SessionManager manager = SessionManager.getInstance();

    public GameClient(String serverUrl) {
        this.serverfacade = ServerFacade.getInstance(serverUrl);
        this.serverUrl = serverUrl;
    }

    public void connectToWebSocket(ServerMessageObserver serverMessageObserver, String auth, int gameId) throws ResponseException {
        this.webSocketFacade = new WebSocketFacade(serverUrl, serverMessageObserver, auth, serverfacade.getGameIdHiderValue(gameId));
    }

    public String eval(String inputLine) {

        board = new PrintBoard(serverfacade.getCurrentUsername());

        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "EXIT" -> leaveGame();
                case "MOVE" -> move(params);
                case "DRAW" -> drawBoard();
                case "RESIGN" -> resign();
                default -> help();

            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String move(String... input) throws ResponseException {
        if (input.length < 1) {
            throw new ResponseException(400, "Move input is required.");
        }
        String moveFrom = input[0];
        String moveTo = input[2];
        try {
            webSocketFacade.makeMove(moveFrom, moveTo);
            return "Move made: " + moveFrom +" to "+ moveTo;
        } catch (ResponseException e) {
            throw new ResponseException(e.StatusCode(), "Failed to make move: " + e.getMessage());
        }
    }

    public String resign() throws ResponseException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Are you sure you want to resign? (yes/no)" + "/n--->");
        String response = scanner.nextLine().trim().toLowerCase();

        if ("yes".equalsIgnoreCase(response)) {
            boolean success = webSocketFacade.resign(serverfacade.getAuth(), serverfacade.getGameIdHiderValue(serverfacade.getGameId()));
            if (success) {
                System.out.println("You have resigned the game.");
                return "success";
            } else {
                System.out.println("Failed to resign the game.");
                return "failed";
            }
        } else {
            System.out.println("Resignation canceled.");
            return "canceled";
        }
    }


    public String leaveGame() throws ResponseException {
        boolean success = webSocketFacade.leave(serverfacade.getAuth(), manager.getGameId(serverfacade.getCurrentUsername()));
        if (success){
            return "Left the Game";
        }
        else {
            return "ERROR: leaving the game failed";
        }
    }

    //save the board as we go so if the server closes or the game is over then we keep the board

    public String drawBoard(){
        board.drawBoard();
        return "Chess Board";
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
