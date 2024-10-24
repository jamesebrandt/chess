package model;

public record LoginResponse(boolean success, String message, String username, String authToken){}
