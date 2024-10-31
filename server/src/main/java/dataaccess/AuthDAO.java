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
        return authTokens.containsKey(token);
    }

    public void deleteAll() {
        // String sql = "DELETE FROM auth_tokens";
        authTokens.clear();
    }


    public String generateToken(String username) {
        if (username == null){return "Cannot Be Null";}

        String token = UUID.randomUUID().toString();
        authTokens.put(token, username);
        return token;
    }

    public int getAuthListSize(){
        return authTokens.size();
    }


    public String getUser(String auth){
        if (!authTokens.containsKey(auth)){return "Invalid Auth Token";}
        return authTokens.get(auth);
    }

    public void deleteAuth(String authToken){
        authTokens.remove(authToken);
    }

    public Map<String, String> getAllAuths(){
        return authTokens;
    }
}