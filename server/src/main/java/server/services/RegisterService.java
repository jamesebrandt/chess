package server.services;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import model.LoginRequest;
import spark.Request;
import dataaccess.UserDAO;
import model.User;
import java.util.UUID;

public class RegisterService {

    private UserDAO userDAO = new UserDAO();
    private AuthDAO AuthDAO = new AuthDAO();


    public String register(Request req) {
        try {
            Gson gson = new Gson();

            Register = gson.fromJson(req.body(), LoginRequest.class);

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
