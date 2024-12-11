package ui;
import Exceptions.ResponseException;
import chess.ChessBoard;

import java.util.Arrays;

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
        String move = input[0];
        try {
            webSocketFacade.makeMove(move);
            return "Move made: " + move;
        } catch (ResponseException e) {
            throw new ResponseException(e.StatusCode(), "Failed to make move: " + e.getMessage());
        }
    }

    public String resign() throws ResponseException {

        // prompt are you sure?


        boolean success = webSocketFacade.resign(serverfacade.getAuth(), serverfacade.getGameId());
        if (success){
            return "success";
        }else{
            return "failed";
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
