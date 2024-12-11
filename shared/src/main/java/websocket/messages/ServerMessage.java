package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.Game;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    Game chessGame;
    String serverMessage;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(Game game, String message, ServerMessageType type) {
        this.chessGame = game;
        this.serverMessage = message;
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }
    public Game getServerMessageGame() {
        return this.chessGame;
    }
    public String getServerMessage() {
        return serverMessage;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
