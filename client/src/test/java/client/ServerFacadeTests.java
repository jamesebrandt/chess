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
    void test1ClearPos() throws Exception {
        try {
            serverFacade.clear();
        } catch (Exception e) {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }



    @Test
    void tes1tRegisterPos() throws Exception {
        var authData = serverFacade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void test1RegisterNeg() throws Exception {
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
    void test1CreateGamePos() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var authData = serverFacade.createGame("testingCreateGame");
        assertTrue(authData.gameID() > 0);
    }

    @Test
    void test1CreateGameNeg() throws Exception {
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
    void test1Observe() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var game = serverFacade.createGame("testingCreateGame");
        assertTrue(!serverFacade.isObserving(game.gameID()));
    }


    @Test
    void test1ListGames() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var game1 = serverFacade.createGame("testingCreateGame1");
        var game2 = serverFacade.createGame("testingCreateGame2");
        var response = serverFacade.listGames();

        assert(!response.games().isEmpty());
    }

    @Test
    void test1ListGamesNeg() throws Exception {
        serverFacade.register("test1","test2", "testemail");
        serverFacade.login("test1","test2");
        var game1 = serverFacade.createGame("testingCreateGame1");
        var game2 = serverFacade.createGame("testingCreateGame2");
        var response = serverFacade.listGames();
        serverFacade.clear();

        assertTrue(!response.games().isEmpty());
    }

    @Test
    void test1LoginPos() throws Exception {
        try {
            serverFacade.register("test1", "test2", "testemail");
            serverFacade.login("test1", "test2");

        } catch (Exception e) {
            fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    @Test
    void test1LoginNeg() throws Exception {
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
    void testClearPos() throws Exception {
        try {
            serverFacade.clear();
            // Assert
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





}
