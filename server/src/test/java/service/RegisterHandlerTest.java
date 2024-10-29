package service;

import com.google.gson.Gson;
import model.RegisterRequest;
import model.RegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.RegisterHandler;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterHandlerTest {
    private RegisterHandler handler;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        handler = new RegisterHandler();
        gson = new Gson();
    }

    @Test
    public void testHandle_BadRequest() {
        Request req = new MockRequest(new RegisterRequest(null, "password", "email"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);
        assertEquals(400, res.status());
        assertEquals("Error: bad request", response.message());
    }

    @Test
    public void test_Success() {

        Request req = new MockRequest(new RegisterRequest("testUser", "password", "email@test.com"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);
        assertEquals(200, res.status());
        assertEquals(true, response.success());
    }

    static class MockRequest extends Request {
        private final RegisterRequest bodyRequest;

        MockRequest(RegisterRequest bodyRequest) {
            this.bodyRequest = bodyRequest;
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