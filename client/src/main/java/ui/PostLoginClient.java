package ui;

import java.util.Arrays;

public class PostLoginClient {

    private final ServerFacade server;

    public PostLoginClient(String serverUrl){
        server = new ServerFacade(serverUrl);
    }


    public String eval(String inputLine){
        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "LOGOUT" -> logout(params);
                case "CREATE_GAME" -> createGame(params);
                case "LIST_GAMES" -> listGames(params);
                case "PLAY_GAME" -> playGame(params);
                case "OBSERVE_GAME" -> observeGame(params);
                case "CLEAR" -> clearGames(params);
                case "QUIT" -> "Quitting Client";
                default -> help();

            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String logout(String... input){
        return "not implemented";
    }

    public String createGame(String... input){
        return "not implemented";
    }

    public String listGames(String... input) {
        return "not implemented";
    }

    public String playGame(String... input) {
        return "playing game";
    }

    public String observeGame(String... input){
        return "not implemented";
    }

    public String clearGames(String... input) throws Exception {
        server.clear();
        return "Server Cleared";
    }

    public String help() {
        return """
                - Logout
                - Create_game <gamename>
                - List_games
                - Play_game <gameID>
                - Observe_game <gameID>
                - Clear
                - Help
                - Quit
                """;
    }
}
