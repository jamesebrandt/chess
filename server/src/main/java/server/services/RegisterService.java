package server.services;
import spark.Request;
import dataaccess.UserDAO;
import model.User;

public class RegisterService {

    private UserDAO userDAO = new UserDAO();

    public boolean register(Request req) {
        try {

            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String email = req.queryParams("email");

            if (userDAO.getUser(username) == null){
                User newUser = new User(username, password, email);
                return userDAO.RegisterUser(newUser);
            } else{
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
