package dataaccessTests;

import dataaccess.UserDAO;
import model.User;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private final UserDAO userDAO = UserDAO.getInstance();

    @Test
    void registerUser() {
        User expected = new User("testUsername",
                "TestPassword",
                "Test@email.com");
        userDAO.registerUser(expected);
        User hashed = userDAO.getUser("testUsername");
        boolean correctpassword = BCrypt.checkpw(expected.password(), hashed.password());

        User expectedForgetPass = new User(expected.username(), null, expected.email());
        User actualForgetPass = new User(hashed.username(), null, hashed.email());

        assertTrue(correctpassword);
        assertEquals(expectedForgetPass, actualForgetPass);
    }

    @Test
    void getUser() {
        User user = new User("testUsername",
                "TestPassword",
                "Test@email.com");

        User expectedRemovedPassword = new User("testUsername",
                null,
                "Test@email.com");


        userDAO.registerUser(user);
        User received = userDAO.getUser("testUsername");
        User actualRemovedPassword = new User(received.username(), null, received.email());
        assertEquals(expectedRemovedPassword, actualRemovedPassword);
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

        assertEquals(expected, userDAO.getAllUsers());
    }
}