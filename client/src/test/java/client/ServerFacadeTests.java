package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        String serverUrl = "http://localhost:8080";
        server = new Server();
        server.run(8080);
        System.out.println("Started test HTTP server on 8080");
        serverFacade = ServerFacade.getInstance(serverUrl);
    }

    @AfterEach
    public void clearDB() throws Exception {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void testClearPos() throws Exception {
        try {
            serverFacade.clear();
        } catch (Exception e) {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    void testRegisterPos() throws Exception {
        var authData = serverFacade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void testRegisterNeg() throws Exception {
        try{
            var authData1 = serverFacade.register("player1", "password", "p1@email.com");
            var authData2 = serverFacade.register("player1", "password", "p1@email.com");
            fail("Should've thrown a RuntimeException for duplicate registration.");
        } catch (RuntimeException e) {
            // Assert
            assertEquals("Failed to Register: Username Taken", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception type: " + e);
        }
    }


    @Test
    void testCreateGamePos() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var authData = serverFacade.createGame("testingCreateGame");
        assertTrue(authData.gameID() > 0);
    }

    @Test
    void testCreateGameNeg() throws Exception {
        try{
            serverFacade.register("test1","test2", "testemail");
            serverFacade.login("test1","test2");
            var authData1 = serverFacade.createGame("testingCreateGame");
            var authData2 = serverFacade.createGame("testingCreateGame");

            fail("Should've thrown a RuntimeException for duplicate game.");
        } catch (RuntimeException e) {
            assertEquals("Failed to Create Game", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception type: " + e);
        }
    }

    @Test
    void testObserve() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var game = serverFacade.createGame("testingCreateGame");
        assertTrue(!serverFacade.isObserving(game.gameID()));
    }


    @Test
    void testListGames() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var game1 = serverFacade.createGame("testingCreateGame1");
        var game2 = serverFacade.createGame("testingCreateGame2");
        var response = serverFacade.listGames();

        assert(!response.games().isEmpty());
    }

    @Test
    void testListGamesNeg() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var game1 = serverFacade.createGame("testingCreateGame1");
        var game2 = serverFacade.createGame("testingCreateGame2");
        var response = serverFacade.listGames();
        serverFacade.clear();

        assertTrue(!response.games().isEmpty());
    }

    @Test
    void testLoginPos() throws Exception {
        try {
            serverFacade.register("test1", "test2", "testemail");
            serverFacade.login("test1", "test2");

        } catch (Exception e) {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    void testLoginNeg() throws Exception {
        try{
            serverFacade.login("notrealname", "notrealpass");
            fail("Should've thrown a RuntimeException for unregistered login.");
        } catch (RuntimeException e) {
            // Assert
            assertEquals("Incorrect Password or Unregistered Account", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception type: " + e);
        }
    }

    @Test
    void testSetCurrentUsername() {
        serverFacade.setCurrentUsername("newUser");
        assertEquals("newUser", serverFacade.getCurrentUsername());
    }

    @Test
    void testGameIdHiderSetAndGet() {
        serverFacade.setGameIdHiderValue(1, 1001);
        assertEquals(1001, serverFacade.getGameIdHiderValue(1));
    }

    @Test
    void testGetGameIdCountAndIndex() {
        int initialCount = serverFacade.getGameIdCountAndIndex();
        assertEquals(initialCount + 1, serverFacade.getGameIdCountAndIndex());
    }

    @Test
    void testJoinGamePos() throws Exception {
        serverFacade.register("testUser", "testPass", "testUser@email.com");
        serverFacade.login("testUser", "testPass");
        var game = serverFacade.createGame("newGame");
        var response = serverFacade.joinGame("WHITE", game.gameID());
        assertNotNull(response);
    }

    @Test
    void testJoinGameNegInvalidTeam() {
        try {
            serverFacade.register("testUser", "testPass", "testUser@email.com");
            serverFacade.login("testUser", "testPass");
            var game = serverFacade.createGame("newGame");
            serverFacade.joinGame("INVALID_TEAM", game.gameID());
            fail("Expected a RuntimeException for invalid team selection");
        } catch (Exception e) {
            assertEquals("Unable to join game", e.getMessage());
        }
    }

    @Test
    void testEmptyGamesListAfterClear() throws Exception {
        serverFacade.register("testUser", "testPass", "testUser@email.com");
        serverFacade.login("testUser", "testPass");
        serverFacade.createGame("newGame1");
        serverFacade.createGame("newGame2");
        serverFacade.clear();
        assertThrows(RuntimeException.class, () -> serverFacade.listGames(), "Empty Games List");
    }


}
