package ui;

public record GameSession(String token, Integer gameId, String team) {
    public GameSession(String token) {
        this(token, null, null); // Default values for optional fields
    }

    public GameSession withGameId(Integer gameId) {
        return new GameSession(this.token, gameId, this.team);
    }

    public GameSession withTeam(String team) {
        return new GameSession(this.token, this.gameId, team);
    }
}