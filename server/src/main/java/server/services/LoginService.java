package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.LoginResponse;

public class LoginService {

    private UserDAO userDAO = UserDAO.getInstance();
    private AuthDAO authDAO = AuthDAO.getInstance();

    public LoginResponse login(LoginRequest req){
        try {


            if (userDAO.checkPassword(req.username(), req.password())) {
                return new LoginResponse(true, "Incorrect Password" , req.username(),  authDAO.generateToken(req.username()));
            }
            else{
                return new LoginResponse(false, "Bad Request" , req.username(),  null);
            }


        }catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(false, "Error" , req.username(),  null);
        }

    }
}
