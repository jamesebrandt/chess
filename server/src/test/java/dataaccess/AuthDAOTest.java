package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class AuthDAOTest {
    private AuthDAO authDAO = AuthDAO.getInstance();



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
        String User1 =authDAO.getUser(auth);
        assertEquals(User1, "Test");
    }

    @Test
    void badUserRequest() {
        authDAO.generateToken("Test");
        String invalid_auth = "c99ee8a2-f2a6-4a04-976a-1a38e9b640cc";
        String User1 =authDAO.getUser(invalid_auth);
        assertEquals(User1, "User not found");
    }

    @Test
    void deleteAuth() {
        Map<String, String> expected = authDAO.getAllAuths();
        String auth3 =authDAO.generateToken("TestUser3");

        authDAO.deleteAuth(auth3);

        assertEquals(authDAO.getAllAuths(), expected);
    }

    @Test
    void connectToSQL() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }



}