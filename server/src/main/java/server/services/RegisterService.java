package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.RegisterRequest;
import model.RegisterResponse;
import model.User;


public class RegisterService {

    private UserDAO userDAO = UserDAO.getInstance();
    private AuthDAO authDAO = AuthDAO.getInstance();

    public RegisterResponse register(RegisterRequest req) {
        try {

            if(userDAO.getUser(req.username()) != null) {
                return new RegisterResponse(false, "Error: already taken", req.username(), null);
            }

            User newUser = new User(req.username(), req.password(), req.email());
            boolean success = userDAO.registerUser(newUser);

            if (success){
                String token = authDAO.generateToken(req.username());
                return new RegisterResponse(true, "Successful Registration", req.username(), token);
            } else {
                return new RegisterResponse(false, "Bad Request", req.username(), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResponse(false, "Error", req.username(), null);
        }
    }
}
