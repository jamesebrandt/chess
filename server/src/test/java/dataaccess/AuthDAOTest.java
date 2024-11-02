package dataaccess;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.CreateGameHandler;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class AuthDAOTest {
    private AuthDAO authDAO = AuthDAO.getInstance();

    @BeforeEach
    public void setUp() {
        deleteAll(); // remove later
    }

    @Test
    void isValidTokenTest() {
        authDAO.generateToken("TestUser1");
        String token = authDAO.generateToken("TestUser2");
        authDAO.generateToken("TestUser3");

        assertTrue(authDAO.isValidToken(token));
    }

    @Test
    void deleteAll() {
        authDAO.generateToken("TestUser1");
        authDAO.generateToken("TestUser2");
        authDAO.generateToken("TestUser3");
        authDAO.deleteAll();
        Assertions.assertEquals(0, authDAO.getAuthListSize());
    }

    @Test
    void generateToken() {
        String auth1 =authDAO.generateToken("TestUser1");
        String auth2 =authDAO.generateToken("TestUser2");
        String auth3 =authDAO.generateToken("TestUser3");

        assert auth1 != null && auth2 != null && auth3 != null : "Tokens should not be null";
        assert !auth1.equals(auth2) : "auth1 and auth2 should be unique";
        assert !auth1.equals(auth3) : "auth1 and auth3 should be unique";
        assert !auth2.equals(auth3) : "auth2 and auth3 should be unique";
    }

    @Test
    void badToken() {
        String auth1 =authDAO.generateToken(null);
        assertEquals( auth1, "Cannot Be Null");
    }

    @Test
    void getUser() {
        String auth = authDAO.generateToken("Test");
        String user1 =authDAO.getUser(auth);
        assertEquals(user1, "Test");
    }

    @Test
    void badUserRequest() {
        authDAO.generateToken("Test");
        String invalidAuth = "c99ee8a2-f2a6-4a04-976a-1a38e9b640cc";
        String user1 =authDAO.getUser(invalidAuth);
        assertEquals(user1, "User not found");
    }

    @Test
    void deleteAuth() {
        Map<String, String> expected = authDAO.getAllAuths();
        String auth3 =authDAO.generateToken("TestUser3");

        authDAO.deleteAuth(auth3);

        assertEquals(expected, authDAO.getAllAuths());
    }

    @Test
    void connectToSQL() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void tryToGetAllTokens() {
        try {
            authDAO.deleteAll();

            String auth1 =authDAO.generateToken("TestUser1");
            String auth2 =authDAO.generateToken("TestUser2");
            String auth3 =authDAO.generateToken("TestUser3");

            Map<String, String> expected = new HashMap<>();
            expected.put(auth1, "TestUser1");
            expected.put(auth2, "TestUser2");
            expected.put(auth3, "TestUser3");

            Map<String, String> actual = authDAO.getAllAuths();

            assertEquals(actual, expected);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        deleteAll(); // remove later
    }


}