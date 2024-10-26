package model;

import java.util.List;
import java.util.Map;

public record GameListResponse(boolean success, String message, List<Map<String, Object>> games) {
}