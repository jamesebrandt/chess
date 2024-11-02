package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class TestUtils {

    public static void cleanupDatabase() {
        AuthDAO.getInstance().deleteAll();
        GameDAO.getInstance().deleteAll();
        UserDAO.getInstance().deleteAll();
    }
}