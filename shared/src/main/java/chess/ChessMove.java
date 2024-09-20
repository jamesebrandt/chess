package chess;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition StartPos;

    @Override
    public String toString() {
        return
                "(" + EndPos.getRow() + "," + EndPos.getColumn() + ")" + ", P:" + promotionPieceType;
    }

    private ChessPosition EndPos;
    private ChessPiece.PieceType promotionPieceType;


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        StartPos = startPosition;
        EndPos = endPosition;
        this.promotionPieceType = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return StartPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return EndPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(StartPos, chessMove.StartPos) && Objects.equals(EndPos, chessMove.EndPos) && promotionPieceType == chessMove.promotionPieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(StartPos, EndPos, promotionPieceType);
    }

}
