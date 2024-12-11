package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.Game;
import model.JoinGameRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageError;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final AuthDAO authDAO = AuthDAO.getInstance();
    private final GameDAO gameDAO = GameDAO.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand genericCommand = gson.fromJson(msg, UserGameCommand.class);
            String username = authDAO.getUser(genericCommand.getAuthToken());

            switch (genericCommand.getCommandType()) {
                case CONNECT -> connect(username, genericCommand, session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = gson.fromJson(msg, MakeMoveCommand.class);
                    makeMove(makeMoveCommand, session);
                }
                case LEAVE -> {
                    LeaveCommand leaveCommand = gson.fromJson(msg, LeaveCommand.class);
                    leaveGame(leaveCommand);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = gson.fromJson(msg, ResignCommand.class);
                    resign(username, resignCommand, session);
                }
            }

            } catch (IllegalArgumentException e) {
            sendErrorMessage(session, "Invalid input: " + e.getMessage());
        } catch (IOException e) {
            sendErrorMessage(session, "I/O error: " + e.getMessage());
        } catch (Exception e) {
            sendErrorMessage(session, "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendErrorMessage(Session session, String errorMsg) {
        try {
            ServerMessageError serverMessageError = new ServerMessageError("ERROR: In WebSocketHandler");
            session.getRemote().sendString(gson.toJson(serverMessageError));
        } catch (IOException e) {
            System.err.println("Failed to send error message: " + errorMsg);
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket closed: " + reason + " (code " + statusCode + ")");
    }

    private void connect(String username, UserGameCommand command, Session session) throws IOException {
        try {

            // Validate auth token
            if (!authDAO.isValidToken(command.getAuthToken())) {
                ServerMessageError serverMessageError = new ServerMessageError("Invalid Auth Token");
                session.getRemote().sendString(serverMessageError.toJson());
            }
            // Validate game ID
            else if (!gameDAO.isValidGameID(command.getGameID())) {
                ServerMessageError serverMessageError = new ServerMessageError("Invalid Game Id");
                session.getRemote().sendString(serverMessageError.toJson());
            }
            else {
                //connect to game
                connections.add(username, command.getAuthToken(), command.getGameID(), session);
                Game game = gameDAO.getGame(command.getGameID());

                //notify all others in the game that they joined
                NotificationMessage notificationMessage = new NotificationMessage(username + " Has Joined game " + game.gameName());
                connections.broadcast(username, notificationMessage);

                // send load game message
                LoadGameMessage loadGameMessage = new LoadGameMessage(game);
                session.getRemote().sendString(loadGameMessage.toJson()); // Send load game message to the connecting player
            }
        } catch (IOException e) {
            throw new IOException(e);
        } catch (IllegalArgumentException e) {
            ServerMessageError serverMessageError = new ServerMessageError(e.getMessage());
            session.getRemote().sendString(serverMessageError.toJson());
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            // Retrieve the game record from the database
            Game gameRecord = gameDAO.getGame(command.getGameID());
            ChessGame chessGame = gameRecord.game();

            // Deserialize the move from the command
            ChessMove move = command.getMove();

            // Validate and execute the move
            if (chessGame.isMoveValid(move)) {
                chessGame.makeMove(move);

                // Update the game record with the modified game state
                Game updatedGameRecord = new Game(
                        gameRecord.gameID(),
                        gameRecord.gameName(),
                        gameRecord.whiteUsername(),
                        gameRecord.blackUsername(),
                        chessGame
                );

                // Notify all players about the move
                LoadGameMessage loadGameMessage = new LoadGameMessage(updatedGameRecord);
                session.getRemote().sendString(loadGameMessage.toJson());

                // Save the updated game state in the database
                gameDAO.saveGame(updatedGameRecord);

            } else {
                sendErrorMessage(session, "ERROR: Invalid move");
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(session, "ERROR: Invalid move: " + e.getMessage());
        } catch (IOException e) {
            sendErrorMessage(session, "Error processing move: " + e.getMessage());
        } catch (InvalidMoveException e) {
            sendErrorMessage(session, "ERROR: Invalid move: " + e.getMessage());
        }
    }


    private void leaveGame(LeaveCommand command){
        int gameId = command.getGameID();
        String user = authDAO.getUser(command.getAuthToken());
        gameDAO.removeUser(new JoinGameRequest(gameDAO.getTeamColor(user), gameId));
    }


    private void resign(String username, ResignCommand command, Session session) {
        try {
            // Retrieve the game record from the database
            Game gameRecord = gameDAO.getGame(command.getGameID());
            ChessGame chessGame = gameRecord.game();

            // Check if the game is already over
            if (chessGame.getIsGameOver()) {
                sendErrorMessage(session, "ERROR: Game is already over.");
                return;
            }

            // Determine the resigning player's team color
            String whitePlayer = gameRecord.whiteUsername();
            String blackPlayer = gameRecord.blackUsername();
            String winningPlayer;

            if (username.equals(whitePlayer)) {
                winningPlayer = blackPlayer != null ? blackPlayer : "No opponent";
            } else if (username.equals(blackPlayer)) {
                winningPlayer = whitePlayer != null ? whitePlayer : "No opponent";
            } else {
                sendErrorMessage(session, "ERROR: User not part of this game.");
                return;
            }

            // Mark the game as over and notify all players
            chessGame.setIsGameOver(true);

            String message = username + " has resigned. " +
                    (winningPlayer.equals("No opponent") ? "No winner." : winningPlayer + " wins!");
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connections.broadcast(username, notificationMessage);

            // Save the updated game state in the database
            Game updatedGameRecord = new Game(
                    gameRecord.gameID(),
                    gameRecord.gameName(),
                    gameRecord.whiteUsername(),
                    gameRecord.blackUsername(),
                    chessGame
            );
            gameDAO.saveGame(updatedGameRecord);

        } catch (IOException e) {
            sendErrorMessage(session, "Error handling resignation: " + e.getMessage());
        } catch (Exception e) {
            sendErrorMessage(session, "Unexpected error during resignation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
