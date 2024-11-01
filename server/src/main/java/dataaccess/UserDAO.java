package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import model.User;

public class UserDAO {

    private final Map<String, User> usersDb = new HashMap<>();
    private static UserDAO instance = null;

    private UserDAO() {}
    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }


//    public boolean registerUser(User user) {
//        if (usersDb.containsKey(user.username())) {
//            return false;
//        }
//        usersDb.put(user.username(), user);
//        return true;
//    }

    public boolean registerUser(User user){
        if(!isUserFree(user)){
            return false;
        }
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.username());
            stmt.setString(2, user.password());
            stmt.setString(3, user.email());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean isUserFree(User user){
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.username());
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1)>0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String username) {
            return usersDb.get(username);
        }

    public boolean checkPassword(String username, String password) {
        User user = usersDb.get(username);
        if (user != null && user.password().equals(password)) {
            return true;
        }
        return false;
    }

    public Map<String, User> getAllUsers(){
        return usersDb;
    }


    public void deleteAll() {
        usersDb.clear();
    }
}
