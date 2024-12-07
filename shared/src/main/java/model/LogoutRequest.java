package model;

public record LogoutRequest(boolean success, String message, String username, String authToken) {
}
