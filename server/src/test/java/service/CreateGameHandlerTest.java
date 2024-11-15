package service;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import model.CreateGameRequest;
import model.CreateGameResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.CreateGameHandler;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateGameHandlerTest {

    private CreateGameHandler handler;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        handler = new CreateGameHandler();
        gson = new Gson();
        TestUtils.cleanupDatabase();
    }

    @AfterAll
    public static void tearDown() {
        TestUtils.cleanupDatabase();
    }


    @Test
    public void testCreateHandleNullAuth() {
        Request req = new MockRequest(null, new CreateGameRequest(null, "TestGame"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        CreateGameResponse response = gson.fromJson(responseJson, CreateGameResponse.class);

        assertEquals(401, res.status());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void testHandleCreateGameSuccess() {
        String authToken = AuthDAO.getInstance().generateToken("Test");
        Request req = new MockRequest(authToken, new CreateGameRequest(null, "TestGame"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        CreateGameResponse response = gson.fromJson(responseJson, CreateGameResponse.class);

        assertEquals(200, res.status());
        assertTrue(response.success());
    }
}