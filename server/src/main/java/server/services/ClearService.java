package server.services;

import server.dao.AuthDAO;
import server.dao.UserDAO;
import server.dao.GameDAO;

public class ClearService {

    public boolean clearAll() {
        try {
            AuthDAO authDAO = new AuthDAO();
            UserDAO userDAO = new UserDAO();
            GameDAO gameDAO = new GameDAO();

            authDAO.deleteAll();  // Clear all auth tokens
            userDAO.deleteAll();  // Clear all users
            gameDAO.deleteAll();  // Clear all games

            return true;  // If successful, return true
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // If something goes wrong, return false
        }
    }
}
