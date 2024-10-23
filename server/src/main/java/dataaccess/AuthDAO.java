package dataaccess;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;


public class AuthDAO {

    private final Map<String, String> authTokens = new HashMap<>();

    public void deleteAll() {
        String sql = "DELETE FROM auth_tokens";
    }


    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        authTokens.put(username, token);
        return token;
    }
    public String getToken(String username) {
        return authTokens.get(username);
    }
}