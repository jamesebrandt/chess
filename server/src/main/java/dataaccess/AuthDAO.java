package dataaccess;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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

//    public boolean isValidToken(String token) {
//        return authTokens.containsKey(token);
//    }

    public boolean isValidToken(String token){
        String query = "SELECT COUNT(*) FROM auth_tokens WHERE token = ?";
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1)>0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }






    public void deleteAll() {
        // String sql = "DELETE FROM auth_tokens";
        authTokens.clear();
    }


//    public String generateToken(String username) {
//        if (username == null){return "Cannot Be Null";}
//
//        String token = UUID.randomUUID().toString();
//        authTokens.put(token, username);
//        return token;
//    }

    public String generateToken(String username) {
        if (username == null){return "Cannot Be Null";}

        String token = UUID.randomUUID().toString();

        String query = "INSERT INTO auth_tokens (token, username) VALUES (?,?)";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, token);
            stmt.setString(2, username);

            stmt.executeUpdate();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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