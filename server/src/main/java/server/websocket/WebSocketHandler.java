package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import org.eclipse.jetty.websocket.api.Session;
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
        }catch (Exception e){
            throw new RuntimeException("Error in onMessage in websocket handler");
        }
    }

    private void connect(String username, UserGameCommand command, Session session) throws IOException {
        try {
            //connect to game
            connections.add(username, command.getAuthToken(), command.getGameID(), session);
            //notify all others in the game that they joined
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(username, message);
            //send error messages if invalid inputs
        }
        catch (IOException e){
            throw new IOException(e);
        }
    }

    private void makeMove(String username, UserGameCommand command, Session session){
        // get the board and make the move
        int game = command.getGameID();


        // serialize the board and send it to each of the viewers

        // save the board in the database

    }

    private void leaveGame(String username, UserGameCommand command, Session session){
    }

    private void resign(String username, UserGameCommand command, Session session){
    }
}
