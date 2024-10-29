package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
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
    }

    @Test
    public void test_NullAuth() {
        Request req = new MockRequest(null);
        Response res = new MockResponse();

        String responseJson = (String) handler.handle(req, res);
        GameListResponse response = gson.fromJson(responseJson, GameListResponse.class);

        assertEquals(401, res.status());
        assertFalse(response.success());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void test_Success() {
        String authToken = AuthDAO.getInstance().generateToken("TestUser");
        GameDAO.getInstance().createGame("TestUser", authToken);

        Request req = new MockRequest(authToken);
        Response res = new MockResponse();

        String responseJson = (String) handler.handle(req, res);
        GameListResponse response = gson.fromJson(responseJson, GameListResponse.class);

        assertEquals(200, res.status());
        assertTrue(response.success());
    }

    static class MockRequest extends Request {
        private final String authToken;

        MockRequest(String authToken) {
            this.authToken = authToken;
        }

        @Override
        public String headers(String header) {
            return "authorization".equals(header) ? authToken : null;
        }

        @Override
        public String body() {
            return "{}";
        }
    }

    static class MockResponse extends Response {
        private int status;
        private String contentType;

        @Override
        public void status(int statusCode) {
            this.status = statusCode;
        }

        @Override
        public int status() {
            return this.status;
        }

        @Override
        public void type(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public String type() {
            return this.contentType;
        }
    }
}