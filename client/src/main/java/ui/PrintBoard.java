package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class PrintBoard {

    private ChessBoard board;
    private boolean isWhitePerspective;
    private SessionManager manager;
    private ServerFacade serverFacade;

    public PrintBoard(String url) {
        this.board = new ChessBoard();
        this.manager = SessionManager.getInstance();
        this.board.resetBoard(); // Initialize the default starting position
        this.serverFacade = ServerFacade.getInstance(url);
    }

    public void PrintBoardForObserver(){
        drawBoard();
        System.out.println();
        drawBoard();
    }


    public void setBoard(ChessBoard customBoard) {
        this.board = customBoard;
    }

    public void drawBoard() {
        isWhitePerspective = manager.getTeam(serverFacade.getCurrentUsername()).equals("WHITE");

        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 1 : 8;
        int stepRow = isWhitePerspective ? -1 : 1;

        int startCol = 1;
        int endCol = 8;
        int stepCol = 1;

        // Loop through rows
        for (int row = startRow; isWhitePerspective ? row >= endRow : row <= endRow; row += stepRow) {
            System.out.print(row + " "); // Row number

            // Loop through columns
            for (int col = startCol; col <= endCol; col += stepCol) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                // Set alternating background colors
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                // Print the appropriate piece or an empty square
                if (piece != null) {
                    System.out.print(getUnicodePiece(piece));
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR); // Reset after each square
            }

            System.out.println(EscapeSequences.RESET_TEXT_COLOR); // Reset color and move to the next line
        }

        // Print column labels
        System.out.print("  ");
        for (int col = startCol; col <= endCol; col++) {
            char columnLabel = (char) ('A' + (col - 1));
            System.out.print(" " + columnLabel + " ");
        }
        System.out.println();
    }


    private String getUnicodePiece(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            default -> EscapeSequences.EMPTY;
        };
    }
}
