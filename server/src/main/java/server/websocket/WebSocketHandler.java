package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
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
            UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);
            String username = authDAO.getUser(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> connect(username, command, session);
                case MAKE_MOVE -> makeMove(username, command, session);
                case LEAVE -> leaveGame(username, command, session);
                case RESIGN -> resign(username, command, session);
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
            //send error messages if invalid inputs
        } catch (IOException e) {
            throw new IOException(e);
        } catch (IllegalArgumentException e) {
            ServerMessageError serverMessageError = new ServerMessageError(e.getMessage());
            session.getRemote().sendString(serverMessageError.toJson());
        }
    }

    private void makeMove(String username, UserGameCommand command, Session session) throws IOException {
        // get the board and make the move

        Game chessGame = gameDAO.getGame(command.getGameID());

        chessGame.game().makeMove(new ChessMove());

        //Game afterMove = currentGame.game().makeMove();


        // serialize the board and send it to each of the viewers

//        ServerMessage message = new ServerMessage(afterMove,
//                username +" has made a move " + command.getGameID(),
//                ServerMessage.ServerMessageType.LOAD_GAME);
//        connections.broadcast(username, message);


        // save the board in the database

    }

    private void leaveGame(String username, UserGameCommand command, Session session){
        int gameId = command.getGameID();
        String user = authDAO.getUser(command.getAuthToken());
        gameDAO.removeUser(new JoinGameRequest(gameDAO.getTeamColor(user), gameId));
    }

    private void resign(String username, UserGameCommand command, Session session){
        //notify the winner/ loser/ observer


        // end the game


        //gameDAO.
    }
}
