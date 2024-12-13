package ui;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private final Map<String, GameSession> sessionTokens;

    // Key is always the username

    private static final SessionManager INSTANCE = new SessionManager();
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public SessionManager() {
        sessionTokens = new HashMap<>();
    }

    public void addSessionToken(String key, String token) {
        sessionTokens.put(key, new GameSession(token));
    }

    public String getSessionToken(String key) {
        GameSession session = sessionTokens.get(key);
        return session != null ? session.token() : null;
    }

    public Integer getGameId(String key) {
        GameSession session = sessionTokens.get(key);
        return session != null ? session.gameId() : null;
    }

    public void setGameId(String key, Integer gameId) {
        GameSession session = sessionTokens.get(key);
        sessionTokens.put(key, session.withGameId(gameId));
    }

    public String getTeam(String key) {
        GameSession session = sessionTokens.get(key);
        return session != null ? session.team() : null;
    }

    public void setTeam(String key, String team) {
        GameSession session = sessionTokens.get(key);
        if (session != null) {
            sessionTokens.put(key, session.withTeam(team));
        }
    }

    //Change your session manager to like a game cash object that just holds all the data for each game.
    // Then use the singleton pattern to deliver it to each object.
    // just have each of the data points be variables in the data cache and use getters and setters to change them


}
