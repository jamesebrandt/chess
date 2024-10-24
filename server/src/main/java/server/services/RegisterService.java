package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.RegisterRequest;
import model.User;


public class RegisterService {

    private UserDAO userDAO = new UserDAO();
    private AuthDAO AuthDAO = new AuthDAO();

    public String register(RegisterRequest req) {
        try {

            if(userDAO.getUser(req.username()) != null) {
                return "Username is Taken";
            }

            User newUser = new User(req.username(), req.password(), req.email());
            boolean success = userDAO.RegisterUser(newUser);

            if (success){
                String token = AuthDAO.generateToken(req.username());
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
