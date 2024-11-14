package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        String serverUrl = "http://localhost:8080";
        Server server = new Server();
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testClear() throws Exception {

        serverFacade.register("test_user", "test_password","test@gmail.com");
        serverFacade.clear();

        assert(serverFacade.)
    }

//    @Test
//    void register() throws Exception {
//        var authData = server.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }


}
