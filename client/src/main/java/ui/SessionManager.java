package ui;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private Map<String, String> sessionTokens;

    public SessionManager() {
        sessionTokens = new HashMap<>();
    }
    public Map<String, String> getSessionTokens() {
        return sessionTokens;
    }
    public void addSessionToken(String key, String token) {
        sessionTokens.put(key, token);
    }
    public String getSessionToken(String key) {
        return sessionTokens.get(key);
    }
}
