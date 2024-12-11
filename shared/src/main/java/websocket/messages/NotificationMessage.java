package websocket.messages;

import com.google.gson.Gson;


public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String notification) {
        super(ServerMessageType.NOTIFICATION);
        this.message = notification;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static NotificationMessage fromJson(String json) {
        return new Gson().fromJson(json, NotificationMessage.class);
    }

}
