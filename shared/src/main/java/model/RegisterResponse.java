package model;

public record RegisterResponse(boolean success, String message, String username, String authToken) {
}
