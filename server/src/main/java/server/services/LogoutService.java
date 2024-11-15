package server.services;

import dataaccess.AuthDAO;
import model.LogoutResponse;

public class LogoutService {

    private final AuthDAO authDAO = AuthDAO.getInstance();

    public LogoutResponse logout(String authToken) {
        try {

            if (authDAO.isValidToken(authToken)) {
                authDAO.deleteAuth(authToken);
                return new LogoutResponse(true, "Successful Logout");
            } else {
                return new LogoutResponse(false, "Error: unauthorized");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new LogoutResponse(false, "Error: " + e.getMessage());
        }
    }
}
