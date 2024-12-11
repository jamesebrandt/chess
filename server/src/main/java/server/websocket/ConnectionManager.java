package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, String auth, int gameId, Session session) {
        var connection = new Connection(username, auth, gameId, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludeUserName, ServerMessage notificationMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUserName)) {
                    System.out.println("Sending message: " + notificationMessage.toJson());
                    c.send(notificationMessage.toJson());
                }
            } else {
                removeList.add(c);
            }
        }
        connections.values().removeAll(removeList);
    }
}
