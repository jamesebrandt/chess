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
        var port = 0;
        String serverUrl = "http://localhost:"+port;
        server = new Server();
        server.run(port);
        System.out.println("Started test HTTP server");
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

    @Test
    void testGetInstance() {
        var port = 0;
        ServerFacade instance1 = ServerFacade.getInstance("http://localhost:"+port);
        ServerFacade instance2 = ServerFacade.getInstance("http://localhost:"+port);
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    @Test
    void testMultipleRegistrationsDifferentUsers() throws Exception {
        var authData1 = serverFacade.register("user1", "password1", "user1@email.com");
        var authData2 = serverFacade.register("user2", "password2", "user2@email.com");
        assertNotEquals(authData1.authToken(), authData2.authToken(),
                "Different users should have different auth tokens");
    }

    @Test
    void testGetCurrentUsernameAfterLogin() throws Exception {
        serverFacade.register("testUser", "password", "testUser@email.com");
        serverFacade.login("testUser", "password");
        assertEquals("testUser", serverFacade.getCurrentUsername(), "Current username should match the logged-in user");
    }

    @Test
    void testClearDatabase() throws Exception {
        serverFacade.register("userForClearTest", "password", "userForClearTest@email.com");
        serverFacade.clear();
        assertThrows(RuntimeException.class, () -> serverFacade.login("userForClearTest",
                "password"), "Database should be cleared and login should fail");
    }

    @Test
    void testDuplicateGameIdsWithHider() {
        int clientId1 = serverFacade.getGameIdCountAndIndex();
        int clientId2 = serverFacade.getGameIdCountAndIndex();
        serverFacade.setGameIdHiderValue(clientId1, 500);
        serverFacade.setGameIdHiderValue(clientId2, 501);
        assertEquals(500, serverFacade.getGameIdHiderValue(clientId1));
        assertEquals(501, serverFacade.getGameIdHiderValue(clientId2));
    }

    @Test
    void testListGamesReturnsCorrectGameNames() throws Exception {
        serverFacade.register("gameListUser", "password", "gamelist@email.com");
        serverFacade.login("gameListUser", "password");
        serverFacade.createGame("game1");
        serverFacade.createGame("game2");
        var response = serverFacade.listGames();

        var gameNames = response.games().stream().map(game -> game.gameName()).toList();
        assertTrue(gameNames.contains("game1") && gameNames.contains("game2"),
                "Game names should match the created games");
    }

    @Test
    void testJoinGameWithoutLogin() {
        assertThrows(RuntimeException.class, () -> serverFacade.joinGame("WHITE", 1),
                "Joining a game without logging in should throw an exception");
    }

    @Test
    void testCreateGameWithoutLogin() {
        assertThrows(RuntimeException.class, () -> serverFacade.createGame("gameWithoutLogin"),
                "Creating a game without logging in should throw an exception");
    }

    @Test
    void testIsObservingFalseByDefault() throws Exception {
        serverFacade.register("observerUser", "password", "observer@email.com");
        serverFacade.login("observerUser", "password");
        var game = serverFacade.createGame("observeTestGame");
        assertFalse(serverFacade.isObserving(game.gameID()),
                "User should not be observing any game by default");
    }

    @Test
    void testSuccessfulLoginDoesNotThrow() {
        assertDoesNotThrow(() -> {
            serverFacade.register("noThrowUser", "password", "nothrow@email.com");
            serverFacade.login("noThrowUser", "password");
        }, "Successful login should not throw any exception");
    }


}
