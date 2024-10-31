package dataaccess;

import model.Game;
import model.JoinGameRequest;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private final GameDAO gameDAO = GameDAO.getInstance();
    private final AuthDAO authDAO = AuthDAO.getInstance();

    @BeforeEach
    void clear(){
        gameDAO.deleteAll();
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

        assertEquals(gameDAO.gameNameAlreadyInUse("TestGame1"), false);
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

        assertEquals(true, gameDAO.isValidGameID(Id));
    }

    @Test
    void isValidColor() {
        assertEquals(false, gameDAO.isValidColor("BLUE"));
        assertEquals(true, gameDAO.isValidColor("WHITE"));
        assertEquals(true, gameDAO.isValidColor("BLACK"));

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
        expected.add(new Game(Id, "TestGame1",null, "TestUserName", null));

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

        int Id2 = gameDAO.createGame("Game2Name", auth1);
        JoinGameRequest request3 = new JoinGameRequest("BLACK", Id2);
        JoinGameRequest request4 = new JoinGameRequest("WHITE", Id2);
        gameDAO.addUsername(request3, "TestUserName4");
        gameDAO.addUsername(request4, "TestUserName5");

        ArrayList<Game> expected = new ArrayList<>();
        expected.add(new Game(Id1, "TestGame1","TestUserName2", "TestUserName1", null));
        expected.add(new Game(Id2, "Game2Name","TestUserName5", "TestUserName4", null));

        assertEquals(expected, gameDAO.listGames());
    }
}