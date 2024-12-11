package websocket.messages;

import com.google.gson.Gson;
import model.Game;

public class LoadGameMessage extends ServerMessage {
    private final Game game;

    public LoadGameMessage(Game game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }


    public String toJson() {
        return new Gson().toJson(this);
    }

    public static NotificationMessage fromJson(String json) {
        return new Gson().fromJson(json, NotificationMessage.class);
    }
}
