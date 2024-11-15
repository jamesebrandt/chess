package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
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

class LoginHandlerTest {
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
    public void testLoginHandleSuccess() {
        String authToken = AuthDAO.getInstance().generateToken("Test");
        Request request = new MockRequest(authToken, new CreateGameRequest(null, "TestGame"));
        Response response = new MockResponse();

        String responseJson = handler.handle(request, response);
        CreateGameResponse responseFromCreateGame = gson.fromJson(responseJson, CreateGameResponse.class);

        assertEquals(200, response.status());
        assertTrue(responseFromCreateGame.success());
    }

}
