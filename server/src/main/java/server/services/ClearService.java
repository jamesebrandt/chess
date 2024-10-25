package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;

public class ClearService {

    public boolean clearAll() {
        try {
            AuthDAO authDAO = AuthDAO.getInstance();
            UserDAO userDAO = UserDAO.getInstance();
            GameDAO gameDAO = GameDAO.getInstance();

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
