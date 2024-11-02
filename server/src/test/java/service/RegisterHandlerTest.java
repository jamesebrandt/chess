package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.RegisterRequest;
import model.RegisterResponse;
import org.junit.jupiter.api.AfterAll;
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

        TestUtils.cleanupDatabase();
    }

    @AfterAll
    public static void tearDown() {
        TestUtils.cleanupDatabase();
    }

    @Test
    public void testHandleBadRequest() {
        Request req = new MockRequest(null, new RegisterRequest(null, "password", "email"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);
        assertEquals(400, res.status());
        assertEquals("Error: bad request", response.message());
    }

    @Test
    public void testSuccess() {

        Request req = new MockRequest(null, new RegisterRequest("testUser", "password", "email@test.com"));
        Response res = new MockResponse();

        String responseJson = handler.handle(req, res);
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);
        assertEquals(200, res.status());
        assertEquals(true, response.success());
    }
}