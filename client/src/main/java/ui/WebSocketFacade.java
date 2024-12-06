package ui;

import Exceptions.ResponseException;
import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;
    ServerFacade serverFacade;


    // create the websocket connection in the constructor
    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;
            this.serverFacade = new ServerFacade(url);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
                        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                        serverMessageObserver.notify(serverMessage);
                    }catch (Exception e){
                        serverMessageObserver.notify(new ServerMessage(ServerMessage.ServerMessageType.ERROR));
                    }
                }
            });
        }catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String move) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, serverFacade.getAuth(), serverFacade.getGameId(), move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String auth, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String auth, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
