package websocket.messages;

import model.Game;

public class ServerMessageError extends ServerMessage{
    private final String errorMessage;

    public ServerMessageError(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
