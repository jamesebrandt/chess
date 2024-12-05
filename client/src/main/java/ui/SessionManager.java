package ui;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private final Map<String, String> sessionTokens;

    public SessionManager() {
        sessionTokens = new HashMap<>();
    }
    public void addSessionToken(String key, String token) {
        sessionTokens.put(key, token);
    }
    public String getSessionToken(String key) {
        return sessionTokens.get(key);
    }
}
