package ui;

import model.CreateGameResponse;
import model.Game;
import model.GameListResponse;

import java.util.ArrayList;
import java.util.Arrays;

public class PostLoginClient {

    private final ServerFacade serverfacade;

    public PostLoginClient(String serverUrl){
        this.serverfacade = ServerFacade.getInstance(serverUrl);
    }


    public String eval(String inputLine){
        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "LOGOUT" -> logout(params);
                case "CREATE_GAME" -> createGame(params);
                case "LIST_GAMES" -> listGames();
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
        if (input.length >= 1) {
            CreateGameResponse createGameResponse = serverfacade.createGame(input[0]);
            if (createGameResponse.success()){
                return "Successfully created game: " + input[1];
            }
            else{
                return "Something went wrong with your game creation";
            }
        }

        throw new RuntimeException("Expected: <gamename>");
    }

    public String listGames() throws Exception {
        GameListResponse gameListResponse =  serverfacade.listGames();
        serverfacade.listGames();

        ArrayList<Game> gameList = gameListResponse.games();

        if (gameList == null || gameList.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < gameList.size(); i++) {
            sb.append(gameList.get(i));
            if (i < gameList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return "List of Current Games: \n"+  sb;
    }

    public String playGame(String... input) {
        return "playing game";
    }

    public String observeGame(String... input){
        return "not implemented";
    }

    public String clearGames(String... input) throws Exception {
        serverfacade.clear();
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
