package dataaccess;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;


public class AuthDAO {

    private final Map<String, String> authTokens = new HashMap<>();

    private static AuthDAO instance = null;

    private AuthDAO() {}
    public static AuthDAO getInstance() {
        if (instance == null) {
            instance = new AuthDAO();
        }
        return instance;
    }

    public boolean isValidToken(String token) {
        return authTokens.containsValue(token);
    }

    public void deleteAll() {
        // String sql = "DELETE FROM auth_tokens";
        authTokens.clear();
    }


    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        authTokens.put(username, token);
        return token;
    }

    public String getToken(String username) {
        for (Map.Entry<String, String> entry : authTokens.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void  deleteAuth(String authToken){
        authTokens.remove(authToken);
    }
}