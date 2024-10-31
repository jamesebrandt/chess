package dataaccess;

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

    public boolean registerUser(User user) {
        if (usersDb.containsKey(user.username())) {
            return false;
        }
        usersDb.put(user.username(), user);
        return true;
    }

    public User getUser(String username) {
        if (usersDb.get(username) != null){
            return usersDb.get(username);}
        throw new IllegalArgumentException("Error: User does not exist");
    }

    public boolean checkPassword(String username, String password) {
        User user = usersDb.get(username);
        if (user != null && user.password().equals(password)) {
            return true;
        }
        return false;
    }


    public void deleteAll() {
        usersDb.clear();
    }
}
