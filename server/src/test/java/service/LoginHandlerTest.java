package service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import server.handlers.CreateGameHandler;


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
}
