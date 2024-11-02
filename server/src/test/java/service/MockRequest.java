package service;

import com.google.gson.Gson;
import spark.Request;

public class MockRequest extends Request {

    private final String authToken;
    private final Object bodyRequest;

    public MockRequest(String authToken, Object bodyRequest) {
        this.authToken = authToken;
        this.bodyRequest = bodyRequest;
    }

    @Override
    public String headers(String header) {
        return "authorization".equals(header) ? authToken : null;
    }

    @Override
    public String body() {
        return new Gson().toJson(bodyRequest);
    }
}