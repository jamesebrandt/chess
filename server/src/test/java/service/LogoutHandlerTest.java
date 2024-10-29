package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
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
    }

    @Test
    public void testHandle_NullAuth() {
        Request req = new MockRequest(null);
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        LogoutResponse response = gson.fromJson(responseJson, LogoutResponse.class);

        assertEquals(401, res.status());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void testSuccess() {
        String authToken = AuthDAO.getInstance().generateToken("Test");
        Request req = new MockRequest(authToken);
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        LogoutResponse response = gson.fromJson(responseJson, LogoutResponse.class);

        assertEquals(200, res.status());
        assertEquals(true, response.success());
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