package ui;

import model.*;

import java.util.*;

public class PostLoginClient {

    private final ServerFacade serverfacade;
    private final Map<String, Integer> listGamesMap = new HashMap<>();
    SessionManager sessionManager;

    public PostLoginClient(String serverUrl){
        this.serverfacade = ServerFacade.getInstance(serverUrl);
        this.sessionManager = SessionManager.getInstance();
    }

    public String eval(String inputLine){
        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "LOGOUT" -> logout(params);
                case "CREATE" -> createGame(params);
                case "LIST" -> listGames();
                case "PLAY" -> playGame(params);
                case "OBSERVE" -> observeGame(params);
                case "CLEAR" -> clearGames(params);
                case "QUIT" -> "Quitting Client";
                default -> help();

            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String logout(String... input) throws Exception {
        serverfacade.logout();
        serverfacade.setCurrentUsername(null);
        return "Logged out! - Auth Token Permanently Terminated";
    }

    public String createGame(String... input){
        if (input.length >= 1) {
            CreateGameResponse createGameResponse = serverfacade.createGame(input[0]);
            if (createGameResponse.success()){
                return "Successfully created game: " + Arrays.toString(input);
            }
            else{
                return "Something went wrong with your game creation";
            }
        }

        throw new RuntimeException("Expected: <gamename>");
    }

    public String listGames() {
        try {
            GameListResponse gameListResponse = serverfacade.listGames();
            ArrayList<Game> gameList = gameListResponse.games();

            if (gameList == null || gameList.isEmpty()) {
                return "[]";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("List of Current Games:\n");

            for (Game game : gameList) {
                String gameName = game.gameName();

                String whiteUser = (game.whiteUsername() == null) ? "OPEN" : game.whiteUsername();
                String blackUser = (game.blackUsername() == null) ? "OPEN" : game.blackUsername();

                String players = "White User: " + whiteUser +
                        "  Black User: " + blackUser;

                if (listGamesMap.containsKey(gameName)) {
                    sb.append(listGamesMap.get(gameName)).append(". ").append(gameName)
                            .append(" - Players: ").append(players).append("\n");
                }else {
                    int id = serverfacade.getNewGameIdCount();
                    serverfacade.indexGameIdCount();

                    sb.append(id).append(". ").append(gameName)
                            .append(" - Players: ").append(players).append("\n");

                    serverfacade.setGameIdHiderValue(id, game.gameID());
                    listGamesMap.put(gameName, id);
                }
            }

            return sb.toString();
        }catch (Exception e){
            throw new RuntimeException("Failed to list games");
        }
    }

    public String playGame(String... input) {
        if (input.length < 2) {
            throw new RuntimeException("Expected: play_game <gameID> <team>");
        }
        int gameId = Integer.parseInt(input[0]);
        String team = input[1];

        JoinGameResponse joinGameResponse = serverfacade.joinGame(team, gameId);

        if (joinGameResponse.success()){
            sessionManager.setTeam(serverfacade.getCurrentUsername(), team);
            sessionManager.setGameId(serverfacade.getCurrentUsername(), gameId);

            return serverfacade.getCurrentUsername() +
                    " has been added to game #" + gameId + " on " + team + " team";
        }
        else{
            return "This games spot is filled already";
        }
    }

    public String observeGame(String... input){
        if (input.length < 1) {
            throw new RuntimeException("Expected: observe <gameID>");
        }
        int gameId = Integer.parseInt(input[0]);


        if (serverfacade.isObserving(gameId)){
            return "Observing game: " + input[0];
        }
        else{
            return "Unable to observe this game";
        }
    }

    public String clearGames(String... input) throws Exception {
        serverfacade.clear();
        return "Server Cleared";
    }

    public String help() {
        return """
                - Logout
                - Create <gamename>
                - List
                - Play <gameID> <team>
                - Observe <gameID>
                - Clear
                - Help
                - Quit
                """;
    }
}
