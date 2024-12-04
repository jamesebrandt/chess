package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;

@websocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    Gson gson = new Gson();
    AuthDAO authDAO = AuthDAO.getInstance();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);

            String username = authDAO.getUser(command.getAuthToken());

            connections.add(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username);
                case MAKE_MOVE -> makeMove(session, username);
                case LEAVE -> leaveGame(session, username);
                case RESIGN -> resign(session, username);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void connect(Session session, String username){
        //connect to game

        //notify all others in the game that they joined

        //send error messages if invalid inputs
    }

    private void makeMove(Session session, String username){
    }

    private void leaveGame(Session session, String username){
    }

    private void resign(Session session, String username){
    }
}
