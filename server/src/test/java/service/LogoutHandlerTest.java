package service;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import model.LogoutResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.LogoutHandler;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogoutHandlerTest {
    private LogoutHandler handler;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        handler = new LogoutHandler();
        gson = new Gson();
        TestUtils.cleanupDatabase();

    }

    @Test
    public void testHandleNullAuth() {
        Request req = new MockRequest(null, null);
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        LogoutResponse response = gson.fromJson(responseJson, LogoutResponse.class);

        assertEquals(401, res.status());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void testSuccess() {
        String authToken = AuthDAO.getInstance().generateToken("Test");
        Request req = new MockRequest(authToken, null);
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        LogoutResponse response = gson.fromJson(responseJson, LogoutResponse.class);

        assertEquals(200, res.status());
        assertEquals(true, response.success());
    }
}