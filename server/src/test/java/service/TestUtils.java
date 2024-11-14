package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class TestUtils {

    public static void cleanupDatabase() {
        AuthDAO.getInstance().deleteAll();
        GameDAO.getInstance().deleteAll();
        UserDAO.getInstance().deleteAll();
    }
}