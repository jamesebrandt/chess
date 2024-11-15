package server.services;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.GameDAO;

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
