package server.handlers;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import server.services.ClearService;

public class ClearHandler {

    public record ClearResponse(boolean success, String message) {}

    public String handle(Request req, Response res) {
        ClearService clearService = new ClearService();
        boolean success = clearService.clearAll();

        ClearResponse response;
        if (success) {
            res.status(200);
            response = new ClearResponse(true, "All data cleared successfully!");
        } else {
            res.status(500);
            response = new ClearResponse(false, "Failed to clear data.");
        }

        Gson gson = new Gson();
        return gson.toJson(response);


    }
}
