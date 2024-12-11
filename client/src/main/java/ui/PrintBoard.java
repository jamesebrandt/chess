package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class PrintBoard {

    private ChessBoard board;
    private boolean isWhitePerspective;
    private SessionManager manager;

    public PrintBoard(String userName) {
        this.board = new ChessBoard();
        this.manager = SessionManager.getInstance();
        this.board.resetBoard(); // Initialize the default starting position

        this.isWhitePerspective = manager.getTeam(userName).equals("WHITE");
    }

    public void PrintBoardForObserver(){

    }


    public void setBoard(ChessBoard customBoard) {
        this.board = customBoard;
    }

    public void drawBoard() {
        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 1 : 8;
        int stepRow = isWhitePerspective ? -1 : 1;

        int startCol = 1;
        int endCol = 8;
        int stepCol = 1;

        for (int row = startRow; isWhitePerspective ? row >= endRow : row <= endRow; row += stepRow) {
            System.out.print(row + " ");

            for (int col = startCol; col <= endCol; col += stepCol) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                // Set background colors for the board squares
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                // Determine the text color and display the piece
                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    } else {
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    }
                    System.out.print(getPieceSymbol(piece));
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }

        // Print column labels
        System.out.print("  ");
        for (int col = startCol; col <= endCol; col++) {
            char columnLabel = (char) ('A' + (col - 1));
            System.out.print(" " + columnLabel + " ");
        }
        System.out.println();
    }

    private char getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case PAWN -> 'P';
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case QUEEN -> 'Q';
            case KING -> 'K';
            default -> '?';
        };
    }
}
