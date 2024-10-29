package service;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import model.JoinGameRequest;
import model.JoinGameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.JoinGameHandler;
import spark.Request;
import spark.Response;
import dataaccess.AuthDAO;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameHandlerTest {

    private JoinGameHandler handler;
    private Gson gson;

    @BeforeEach
    void setUp() {
        handler = new JoinGameHandler();
        gson = new Gson();
    }

    @Test
    void testHandleSuccess() {
        String authToken = AuthDAO.getInstance().generateToken("TestUser");
        GameDAO.getInstance().createGame("TestUser", authToken);


        Request req = new MockRequest(authToken, new JoinGameRequest("WHITE", 10));
        Response res = new MockResponse();

        JoinGameResponse response = gson.fromJson(handler.handle(req, res), JoinGameResponse.class);

        assertEquals(200, res.status());
        assertTrue(response.success());
    }

    @Test
    void testHandleBadRequest() {
        String authToken = AuthDAO.getInstance().generateToken("TestUser");

        Request req = new MockRequest(authToken, new JoinGameRequest(null, 10));
        Response res = new MockResponse();

        JoinGameResponse response = gson.fromJson(handler.handle(req, res), JoinGameResponse.class);

        assertEquals(400, res.status());
        assertFalse(response.success());
        assertEquals("Error: bad request", response.message());
    }

    // Minimal mock classes for Request and Response
    static class MockRequest extends Request {
        private final String authToken;
        private final JoinGameRequest bodyRequest;

        MockRequest(String authToken, JoinGameRequest bodyRequest) {
            this.authToken = authToken;
            this.bodyRequest = bodyRequest;
        }

        @Override
        public String headers(String header) {
            return "authorization".equals(header) ? authToken : null;
        }

        @Override
        public String body() {
            return new Gson().toJson(bodyRequest);
        }
    }

    static class MockResponse extends Response {
        private int status;

        @Override
        public void status(int statusCode) {
            this.status = statusCode;
        }

        @Override
        public int status() {
            return this.status;
        }
    }
}
