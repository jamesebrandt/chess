package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String username;
    public String auth;
    public int gameId;
    public Session session;

    public Connection(String username, String auth, int gameId, Session session) {
        this.username = username;
        this.auth = auth;
        this.gameId = gameId;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
