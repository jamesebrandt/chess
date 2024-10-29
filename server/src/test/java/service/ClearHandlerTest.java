package service;

import org.junit.jupiter.api.Test;
import server.handlers.ClearHandler;

import static org.junit.jupiter.api.Assertions.*;

class ClearHandlerTest {

    @Test
    void handle() {
        assertEquals(new ClearHandler.ClearResponse(true, "All data cleared successfully!"), new ClearHandler.ClearResponse(true, "All data cleared successfully!"));
    }
}