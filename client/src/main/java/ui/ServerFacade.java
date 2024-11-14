package ui;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import model.*;

public class ServerFacade {

    SessionManager manager = new SessionManager();
    String currentUsername;
    private final String serverUrl;
    public ServerFacade(String url){
        serverUrl = url;
        this.manager = new SessionManager();
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
            throw new RuntimeException("Failed to Register");
        }
    }

    public GameListResponse listGames() throws Exception{
        try {
            var path = "/game";
            GameListRequest gameListRequest = new GameListRequest(manager.getSessionToken(currentUsername));
            return this.makeRequest("POST", path, gameListRequest, GameListResponse.class);
        }catch (Exception e){
            throw new RuntimeException("Failed to List Games");
        }
    }

    public LoginResponse login(String username, String password) throws Exception{
        try {
            var path = "/user";
            LoginRequest loginRequest = new LoginRequest(username, password, null);
            return this.makeRequest("POST", path, loginRequest, LoginResponse.class);
        }catch (Exception e){
            throw new RuntimeException("Failed to Register");
        }
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
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