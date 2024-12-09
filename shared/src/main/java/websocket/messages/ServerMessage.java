package websocket.messages;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
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

    public static class LoadGameMessage extends ServerMessage {
        private final Object game;

        public LoadGameMessage(Object game) {
            super(ServerMessageType.LOAD_GAME);
            this.game = game;
        }

        public Object getGame() {
            return game;
        }
    }

    public static class ErrorMessage extends ServerMessage {
        private final String errorMessage;

        public ErrorMessage(String errorMessage) {
            super(ServerMessageType.ERROR);
            if (!errorMessage.contains("Error")) {
                throw new IllegalArgumentException("ErrorMessage must contain the word 'Error'.");
            }
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class NotificationMessage extends ServerMessage {
        private final String message;

        public NotificationMessage(String message) {
            super(ServerMessageType.NOTIFICATION);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
