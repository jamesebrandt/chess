package server.services;
import dataaccess.AuthDAO;
import spark.Request;
import dataaccess.UserDAO;
import model.User;
import java.util.UUID;

public class RegisterService {

    private UserDAO userDAO = new UserDAO();
    private AuthDAO AuthDAO = new AuthDAO();


    public String register(Request req) {
        try {

            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String email = req.queryParams("email");

            if(userDAO.getUser(username) != null) {
                return "Username is Taken";
            }

            User newUser = new User(username, password, email);
            boolean success = userDAO.RegisterUser(newUser);

            if (success){
                String token = AuthDAO.generateToken(username);
                return "Successful Registration! Token: " + token;
            } else {
                return "Bad Request";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
