package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameListRequest;
import model.GameListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.GameListHandler;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.*;

class GameListHandlerTest {

    private GameListHandler handler;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        handler = new GameListHandler();
        gson = new Gson();
        TestUtils.cleanupDatabase();

    }

    @Test
    public void testNullAuth() {
        Request req = new MockRequest(null, null);
        Response res = new MockResponse();

        String responseJson = (String) handler.handle(req, res);
        GameListResponse response = gson.fromJson(responseJson, GameListResponse.class);

        assertEquals(401, res.status());
        assertFalse(response.success());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void testSuccess() {
        String authToken = AuthDAO.getInstance().generateToken("TestUser");
        GameDAO.getInstance().createGame("TestUser", authToken);

        Request req = new MockRequest(authToken, new GameListRequest(authToken));
        Response res = new MockResponse();

        String responseJson = (String) handler.handle(req, res);
        GameListResponse response = gson.fromJson(responseJson, GameListResponse.class);

        assertEquals(200, res.status());
        assertTrue(response.success());
    }}