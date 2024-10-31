package dataaccess;

import model.User;
import org.junit.jupiter.api.Test;
import dataaccess.GameDAO;
import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private final UserDAO userDAO = UserDAO.getInstance();

    @Test
    void registerUser() {
        User user = new User("testUsername",
                "TestPassword",
                "Test@email.com");
        userDAO.registerUser(user);

        assertEquals(userDAO.getUser("testUsername"), user);
    }

    @Test
    void getUser() {
        User user = new User("testUsername",
                "TestPassword",
                "Test@email.com");
        userDAO.registerUser(user);
        assertEquals(userDAO.getUser("testUsername"), user);
    }

    @Test
    void getUserBadRequest() {
        User failure = userDAO.getUser("TestUser");



    }

    @Test
    void checkPassword() {
    }

    @Test
    void deleteAll() {
    }
}