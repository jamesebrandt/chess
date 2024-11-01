package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.JoinGameService;
import server.services.RegisterService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private final GameDAO gameDAO = GameDAO.getInstance();
    private final AuthDAO authDAO = AuthDAO.getInstance();
    private final UserDAO userDAO = UserDAO.getInstance();

    @BeforeEach
    void clear() throws DataAccessException {
        gameDAO.deleteAll();
        DatabaseManager.configureDatabase();
    }

    @Test
    void deleteAll() {
        User user = new User("TestUser", "TestPassword", "Test1@gmail.com");
        String auth = authDAO.generateToken(user.username());

        ArrayList<Game> expected = new ArrayList<>();

        gameDAO.createGame("TestGame1", auth);
        gameDAO.createGame("TestGame2", auth);
        gameDAO.createGame("TestGame3", auth);
        gameDAO.deleteAll();
        assertEquals(gameDAO.listGames(), expected);
    }

    @Test
    void gameNameAlreadyInUse() {
        User user = new User("TestUser", "TestPassword", "Test1@gmail.com");
        String auth = authDAO.generateToken(user.username());

        gameDAO.createGame("TestGame1", auth);

        assertTrue(gameDAO.gameNameAlreadyInUse("TestGame1"));
    }

    @Test
    void checkIfStealing() {
        try {
            RegisterService registerService = new RegisterService();
            JoinGameService joinGameService = new JoinGameService();

            RegisterRequest registerUser1Request = new RegisterRequest("TestUser1", "TestPassword", "Test1@gmail.com");
            RegisterResponse response1 = registerService.register(registerUser1Request);

            RegisterRequest registerUser2Request = new RegisterRequest("TestUser2", "TestPassword", "Test2@gmail.com");
            RegisterResponse response2 = registerService.register(registerUser2Request);

            int gameId = gameDAO.createGame("TestGame1", response1.authToken());

            JoinGameRequest joinGameRequest1 = new JoinGameRequest("BLACK", gameId);
            String result1 = (joinGameService.joinGame(joinGameRequest1, response1.authToken())).message();

            JoinGameRequest joinGameRequest2 = new JoinGameRequest("BLACK", gameId);
            String result2 = (joinGameService.joinGame(joinGameRequest2, response2.authToken())).message();

            assertEquals("Added!", result1);
            assertEquals("Error: already taken", result2);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createGame() {
        User user = new User("TestUser", "TestPassword", "Test1@gmail.com");
        String auth = authDAO.generateToken(user.username());

        int Id = gameDAO.createGame("TestGame1", auth);

        assertEquals(Id+1, gameDAO.createGame("TestGame", auth));
    }

    @Test
    void isValidGameID() {
        User user = new User("TestUser", "TestPassword", "Test1@gmail.com");
        String auth = authDAO.generateToken(user.username());

        int Id = gameDAO.createGame("TestGame1", auth);

        assertTrue(gameDAO.isValidGameID(Id));
    }

    @Test
    void isValidColor() {
        assertFalse(gameDAO.isValidColor("BLUE"));
        assertTrue(gameDAO.isValidColor("WHITE"));
        assertTrue(gameDAO.isValidColor("BLACK"));

    }

    @Test
    void isStealingTeamColor() {
//        JoinGameRequest request = new JoinGameRequest("BLACK", 10);
//        boolean test = gameDAO.isStealingTeamColor(request);
//
//        assertEquals(true, test);
    }

    @Test
    void addUsername() {
        User user = new User("TestUser", "TestPassword", "Test1@gmail.com");
        String auth = authDAO.generateToken(user.username());
        int Id = gameDAO.createGame("TestGame1", auth);
        JoinGameRequest request = new JoinGameRequest("BLACK", Id);
        gameDAO.addUsername(request, "TestUserName");

        ArrayList<Game> expected = new ArrayList<>();
        ChessGame defaultChessGame = new ChessGame();
        expected.add(new Game(Id, "TestGame1",null, "TestUserName", defaultChessGame));

        assertEquals(expected, gameDAO.listGames());
    }

    @Test
    void listGames() {
        User user1 = new User("TestUser", "TestPassword", "Test1@gmail.com");
        String auth1 = authDAO.generateToken(user1.username());
        int Id1 = gameDAO.createGame("TestGame1", auth1);
        JoinGameRequest request1 = new JoinGameRequest("BLACK", Id1);
        JoinGameRequest request2 = new JoinGameRequest("WHITE", Id1);
        gameDAO.addUsername(request1, "TestUserName1");
        gameDAO.addUsername(request2, "TestUserName2");


        ArrayList<Game> expected = new ArrayList<>();
        ChessGame defaultChessGame = new ChessGame();

        expected.add(new Game(Id1, "TestGame1","TestUserName2", "TestUserName1", defaultChessGame));

        assertEquals(expected, gameDAO.listGames());
    }
}