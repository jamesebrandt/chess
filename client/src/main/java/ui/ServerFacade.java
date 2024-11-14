package ui;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import model.*;

public class ServerFacade {

    private static ServerFacade instance;
    private String currentUsername;
    private final String serverUrl;
    private SessionManager manager = new SessionManager();
    private Map<Integer, Integer> gameIdHider = new HashMap<>();
    private int gameIdCount = 1;


    public ServerFacade(String url){
        this.serverUrl = url;
        this.manager = new SessionManager();
    }

    public static synchronized ServerFacade getInstance(String serverUrl) {
        if (instance == null) {
            instance = new ServerFacade(serverUrl);
        }
        return instance;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public void setGameIdHiderValue(int clientGameId, int serverGameId){
        gameIdHider.put(clientGameId, serverGameId);
    }

    public int getGameIdHiderValue(int clientGameId){
        return gameIdHider.get(clientGameId);
    }

    public void clear() throws Exception{
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    public RegisterResponse register(String username, String password, String email) throws Exception{
        try {
            var path = "/user";
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResponse registerResponse = this.makeRequest("POST", path, registerRequest, RegisterResponse.class);
            manager.addSessionToken(registerResponse.username(), registerResponse.authToken());
            currentUsername = registerResponse.username();
            return registerResponse;
        }catch (Exception e){
            throw new RuntimeException("Failed to Register: Username Taken");
        }
    }

    public CreateGameResponse createGame(String gameID){
        try {
            var path = "/game";
            CreateGameRequest createGameRequest = new CreateGameRequest(manager.getSessionToken(currentUsername), gameID);
            CreateGameResponse createGameResponse = this.makeRequest("POST", path, createGameRequest, CreateGameResponse.class);
            return createGameResponse;
        }catch (Exception e){
            throw new RuntimeException("Failed to Create Game");
        }
    }

    public GameListResponse listGames() throws Exception {
        try {
            var path = "/game";
            String token = manager.getSessionToken(currentUsername);
            return this.makeRequest("GET", path, null, GameListResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to List Games");
        }
    }


    public LoginResponse login(String username, String password) throws Exception{
        try {
            var path = "/session";
            LoginRequest loginRequest = new LoginRequest(username, password, null);
            LoginResponse loginResponse = this.makeRequest("POST", path, loginRequest, LoginResponse.class);

            manager.addSessionToken(loginResponse.username(), loginResponse.authToken());
            currentUsername = loginResponse.username();

            return loginResponse;

        }catch (Exception e){
            throw new RuntimeException("Incorrect Password or Unregistered Account");
        }
    }

    public JoinGameResponse joinGame(String team, int id) throws Exception{
        try {
            var path = "/game";
            if (!team.equals("WHITE") && !team.equals("BLACK")) {
                throw new RuntimeException("You must select 'WHITE' or 'BLACK' as team color");
            }


            JoinGameRequest joinGameRequest = new JoinGameRequest(team, id);
            return this.makeRequest("PUT", path, joinGameRequest, JoinGameResponse.class);

        }catch (Exception e){
            throw new RuntimeException("Register before you try to sign in");
        }
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (!method.equals("GET")) {
                http.setDoOutput(true);
            }

            String token = manager.getSessionToken(currentUsername);
            if (token != null && !token.isEmpty()) {
                http.addRequestProperty("Authorization", token);
            }

            if (request != null && !method.equals("GET")) {
                writeBody(request, http);
            }

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new RuntimeException();
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}