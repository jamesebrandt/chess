package model;

import java.util.ArrayList;

public record GameListResponse(boolean success, String message, ArrayList<Game> games) {
}