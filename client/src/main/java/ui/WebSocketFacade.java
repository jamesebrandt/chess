package ui;

import Exceptions.ResponseException;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.Game;
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
    Game updatedGame;
    ChessGame chessGame;


    // create the websocket connection in the constructor
    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver, String authToken, Integer gameID) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;
            this.serverFacade = ServerFacade.getInstance(url);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.chessGame = new ChessGame();

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

            UserGameCommand connectCommand = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT, authToken, gameID
            );
            String commandJson = new Gson().toJson(connectCommand);
            this.session.getAsyncRemote().sendText(commandJson);

        }catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String move) throws ResponseException {
        try {
            // read the input string and translate to ChessMove
            int startRow = move.charAt(0);
            int startCol = move.charAt(1);

            ChessPosition startPosition = new ChessPosition(startRow, startCol);

            int endRow = move.charAt(2);
            int endCol = move.charAt(3);

            ChessPosition endPosition = new ChessPosition(startRow, startCol);
            ChessMove chessMove = new ChessMove(startPosition, endPosition, null);

            // test if the move is valid locally before making the request
            chessGame.makeMove(chessMove);
            //
            var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, serverFacade.getAuth(), serverFacade.getGameId());
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean leave(String auth, int gameID) throws ResponseException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, serverFacade.getGameIdHiderValue(gameID));
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            return true;
        } catch (Exception e) {
            return false;
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
