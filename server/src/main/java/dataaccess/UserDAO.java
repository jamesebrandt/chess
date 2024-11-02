package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import model.User;
import org.mindrot.jbcrypt.BCrypt;

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

        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.username());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public User getUser(String username){

        String query = "SELECT username, password, email FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try(ResultSet rs = stmt.executeQuery();){

                if (rs.next()) {
                    String userUsername = rs.getString("username");
                    String userPassword = rs.getString("password");
                    String userEmail = rs.getString("email");

                    return new User(userUsername, userPassword, userEmail);
                }
                else{
                    return null;
                }
            }

        } catch (Exception e) {
            throw new ResponseException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }


    public boolean checkPassword(String username, String password) {
        User user = getUser(username);
        if (user == null){
            return false;
        }
        if (BCrypt.checkpw(password, user.password())) {
            return true;
        }
        return false;
    }

    public Map<String, User> getAllUsers(){

        Map<String, User> userList = new HashMap<>();
        String query = "SELECT username, password, email FROM users";

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            try (ResultSet rs = stmt.executeQuery();) {

                while (rs.next()) {
                    String userUsername = rs.getString("username");
                    String userPassword = rs.getString("password");
                    String userEmail = rs.getString("email");

                    User user = new User(userUsername, userPassword, userEmail);

                    userList.put(userUsername, user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return userList;
    }


    public void deleteAll() {

        String query = "TRUNCATE TABLE users";

        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
