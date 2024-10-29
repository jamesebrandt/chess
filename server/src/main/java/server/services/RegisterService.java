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
                return new RegisterResponse(false, "Error: already taken", null, null);
            }
            if(req.password() == null){
                return new RegisterResponse(false, "Error: bad request", null, null);
            }

            User newUser = new User(req.username(), req.password(), req.email());
            boolean success = userDAO.registerUser(newUser);

            if (success){
                return new RegisterResponse(true, "Successful Registration", req.username(), authDAO.generateToken(req.username()));
            } else {
                return new RegisterResponse(false, "Error: bad request", null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResponse(false, "Error", null, null);
        }
    }
}
