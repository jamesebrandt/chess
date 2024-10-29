package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import model.CreateGameRequest;
import model.CreateGameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.CreateGameHandler;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginHandlerTest {
    private CreateGameHandler handler;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        handler = new CreateGameHandler();
        gson = new Gson();
    }

    @Test
    public void testHandleNullAuth() {
        Request req = new MockRequest(null, new CreateGameRequest(null, "TestGame"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        CreateGameResponse response = gson.fromJson(responseJson, CreateGameResponse.class);

        assertEquals(401, res.status());
        assertEquals("Error: unauthorized", response.message());
    }

    @Test
    public void testHandleSuccess() {
        String authToken = AuthDAO.getInstance().generateToken("Test");
        Request req = new MockRequest(authToken, new CreateGameRequest(null, "TestGame"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        CreateGameResponse response = gson.fromJson(responseJson, CreateGameResponse.class);

        assertEquals(200, res.status());
        assertEquals(true, response.success());
    }

    static class MockRequest extends Request {
        private final String authToken;
        private final CreateGameRequest bodyRequest;

        MockRequest(String authToken, CreateGameRequest bodyRequest) {
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