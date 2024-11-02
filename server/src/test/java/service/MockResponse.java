package service;
import spark.Response;

public class MockResponse extends Response {

    private int status;

    @Override
    public void status(int statusCode) {
        this.status = statusCode;
    }

    @Override
    public int status() {
        return this.status;
    }
}