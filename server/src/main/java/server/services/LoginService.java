package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.LoginRequest;

public class LoginService {

    private UserDAO userDAO = new UserDAO();
    private AuthDAO authDAO = new AuthDAO();

    public String login(LoginRequest req){
        try {


            if(userDAO.getUser(req.username()) == null) {
                return "Account does not exist";
            } else if (userDAO.checkPassword(req.username(), req.password())) {
                return "Logged in! Token: " + authDAO.generateToken(req.username());
            }
            else{
                return "Incorrect Password";
            }


        }catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

    }
}
