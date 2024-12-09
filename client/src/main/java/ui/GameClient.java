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
        this.webSocketFacade = new WebSocketFacade(serverUrl, serverMessageObserver, auth, gameId);
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
