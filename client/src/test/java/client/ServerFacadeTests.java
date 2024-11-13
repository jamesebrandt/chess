package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

//    @BeforeEach
//    static void clearDataBase(){
//        server
//    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

//    @Test
//    void register() throws Exception {
//        var authData = server.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }


}