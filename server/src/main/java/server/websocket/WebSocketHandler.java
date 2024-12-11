package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.JoinGameRequest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    Gson gson = new Gson();
    AuthDAO authDAO = AuthDAO.getInstance();
    GameDAO gameDAO = GameDAO.getInstance();

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
            ServerMessage errorMessage = new ServerMessage(
                    null,
                    errorMsg,
                    ServerMessage.ServerMessageType.ERROR
            );
            session.getRemote().sendString(gson.toJson(errorMessage));
        } catch (IOException e) {
            System.err.println("Failed to send error message: " + errorMsg);
            e.printStackTrace();
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error for session: " + session.getRemoteAddress());
        throwable.printStackTrace();
        try {
            if (session != null && session.isOpen()) {
                ServerMessage errorMessage = new ServerMessage(
                        null,
                        "An error occurred: " + throwable.getMessage(),
                        ServerMessage.ServerMessageType.ERROR
                );
                session.getRemote().sendString(gson.toJson(errorMessage));
            }
        } catch (IOException e) {
            System.err.println("Failed to send error message to client.");
            e.printStackTrace();
        }
    }


    private void connect(String username, UserGameCommand command, Session session) throws IOException {
        try {

            // Validate auth token
            if (!authDAO.isValidToken(username)) {
                ServerMessage errorMessage = new ServerMessage(null, "invalid Auth", ServerMessage.ServerMessageType.ERROR);
            }

            // Validate game ID
            if (!gameDAO.isValidGameID(command.getGameID())) {
                throw new IllegalArgumentException("Invalid game ID: " + command.getGameID());
            }

            //connect to game
            connections.add(username, command.getAuthToken(), command.getGameID(), session);
            //notify all others in the game that they joined
            ServerMessage message = new ServerMessage(null,
                    "Connecting to Game: "+ command.getGameID(),
                    ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(username, message);
            //send error messages if invalid inputs
        }
        catch (IOException e){
            throw new IOException(e);
        }
    }

    private void makeMove(String username, UserGameCommand command, Session session){
        // get the board and make the move


        // serialize the board and send it to each of the viewers

        // save the board in the database

    }

    private void leaveGame(String username, UserGameCommand command, Session session){
        int gameId = command.getGameID();
        String user = authDAO.getUser(command.getAuthToken());

        gameDAO.removeUser(new JoinGameRequest(gameDAO.getTeamColor(user), gameId));

    }

    private void resign(String username, UserGameCommand command, Session session){
    }
}
