package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import spark.Request;
import spark.Response;

public class LoginService {

    private UserDAO userDAO = new UserDAO();
    private AuthDAO authDAO = new AuthDAO();

    public String login(Request req){
        try {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if(userDAO.getUser(username) == null) {
                return "Account does not exist";
            } else if (userDAO.checkPassword(username, password)) {

            }






        }catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }

    }
}
