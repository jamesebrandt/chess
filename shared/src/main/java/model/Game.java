package model;

import chess.ChessGame;

public record Game(Integer gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game) {
}
