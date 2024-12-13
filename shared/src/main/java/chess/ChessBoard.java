package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }


    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = board.length - 1; row >= 0; row--) {
            sb.append(row + 1).append(" ");

            for (int col = 0; col < board[row].length; col++) {
                ChessPiece piece = board[row][col];

                if (piece == null) {
                    sb.append(". ");  // Empty square
                } else {
                    char symbol = getPieceSymbol(piece);
                    sb.append(symbol).append(" ");
                }
            }

            sb.append("\n");
        }

        sb.append("  1 2 3 4 5 6 7 8\n");
        return sb.toString();
    }

    private char getPieceSymbol(ChessPiece piece) {
        char symbol;
        switch (piece.getPieceType()) {
            case PAWN:   symbol = 'P'; break;
            case ROOK:   symbol = 'R'; break;
            case KNIGHT: symbol = 'N'; break;
            case BISHOP: symbol = 'B'; break;
            case QUEEN:  symbol = 'Q'; break;
            case KING:   symbol = 'K'; break;
            default:     symbol = '?'; break;
        }

        return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? Character.toLowerCase(symbol) : symbol;
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KING);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KING);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

}
