package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;

public class ClearService {

    public boolean clearAll() {
        try {
            AuthDAO authDAO = new AuthDAO();
            UserDAO userDAO = UserDAO.getInstance();
            GameDAO gameDAO = new GameDAO();

            authDAO.deleteAll();
            userDAO.deleteAll();
            gameDAO.deleteAll();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
