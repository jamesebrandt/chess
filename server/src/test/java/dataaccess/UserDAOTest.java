package dataaccess;

import model.User;
import org.junit.jupiter.api.Test;
import dataaccess.GameDAO;

import java.util.HashMap;
import java.util.Map;

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
        String failure = "TestUser";

        assertThrows(IllegalArgumentException.class, () -> {
            userDAO.getUser(failure);
        });
    }

    @Test
    void checkPassword() {
        User user = new User("testUsername",
                "TestPassword",
                "Test@email.com");
        userDAO.registerUser(user);

        assertEquals(userDAO.checkPassword("testUsername","TestPassword"), true);
    }


    @Test
    void checkBadPassword() {
        User user = new User("testUsername",
                "TestPassword",
                "Test@email.com");
        userDAO.registerUser(user);

        assertEquals(userDAO.checkPassword("testUsername","fail"), false);
    }

    @Test
    void deleteAll() {
        Map<String, User> expected = new HashMap<>();

        User user1 = new User("testUsername1",
                "TestPassword1",
                "Test1@email.com");
        userDAO.registerUser(user1);

        User user2 = new User("testUsername2",
                "TestPassword2",
                "Test2@email.com");
        userDAO.registerUser(user2);

        User user3 = new User("testUsername3",
                "TestPassword3",
                "Test3@email.com");
        userDAO.registerUser(user3);

        userDAO.deleteAll();

        assertEquals(userDAO.getAllUsers(), expected);
    }
}