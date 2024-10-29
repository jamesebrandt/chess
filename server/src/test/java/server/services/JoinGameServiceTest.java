package server.services;

import model.JoinGameRequest;
import model.JoinGameResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTest {

    @Test
    void joinGame() {
        assertEquals(new JoinGameRequest("WHITE", 10), new JoinGameRequest("WHITE", 10));
    }
}