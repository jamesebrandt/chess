package dataaccess;
import java.util.HashMap;
import java.util.Map;
import model.User;

public class UserDAO {

    private final Map<String, User> usersDb = new HashMap<>();

    public boolean RegisterUser(User user){
        if (usersDb.containsKey(user.username())){
            return false;
        }
        usersDb.put(user.username(),user);
        return true;
    }
    public User getUser(String username){
        return usersDb.get(username);
    }

    public boolean checkPassword(String username, String password) {
        User user = usersDb.get(username);
        if (user.getPassword() == password){
            return true;
        }
        return false;
    }

    public boolean deleteUSer(String username){
        return usersDb.remove(username) != null;
    }

    public void deleteAll(){
        String sql = "DELETE FROM Users";
        usersDb.clear();
    }


}
