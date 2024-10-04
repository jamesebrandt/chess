package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.pieceColor = pieceColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, pieceColor);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChessPiece.class.getSimpleName() + "[", "]")
                .add("" + pieceType)
                .add("" + pieceColor)
                .toString();
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    private PieceType pieceType;
    private ChessGame.TeamColor pieceColor;

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        PieceMovesCalculator calculator = new PieceMovesCalculator(board, position);
        ChessPiece piece = board.getPiece(position);

        if (piece == null) {
            return Collections.emptyList();
        }

        return calculator.calculateMoves(piece, position);
    }

    public class PieceMovesCalculator {

        private ChessBoard board;
        private ChessPosition position;
        private Collection<ChessMove> moves;
        private ChessGame.TeamColor teamColor;

        public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
            this.board = board;
            this.position = position;
            this.moves = new ArrayList<>();
            this.teamColor = board.getPiece(position).getTeamColor();
        }

        public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessPosition position) {
            switch (piece.getPieceType()) {
                case KING:
                    addKingMoves(position);
                    break;
                case QUEEN:
                    moveInDirections(position, 1, 1);
                    moveInDirections(position, -1, 1);
                    moveInDirections(position, -1, -1);
                    moveInDirections(position, 1, -1);
                    moveInDirections(position, 1, 0);
                    moveInDirections(position, 0, 1);
                    moveInDirections(position, -1, 0);
                    moveInDirections(position, 0, -1);
                    break;
                case BISHOP:
                    moveInDirections(position, 1, 1);
                    moveInDirections(position, -1, 1);
                    moveInDirections(position, -1, -1);
                    moveInDirections(position, 1, -1);
                    break;
                case KNIGHT:
                    addKnightMoves(position);
                    break;
                case ROOK:
                    moveInDirections(position, 1, 0);
                    moveInDirections(position, 0, 1);
                    moveInDirections(position, -1, 0);
                    moveInDirections(position, 0, -1);
                    break;
                case PAWN:
                    addPawnMoves(position);
                    break;
            }
            return moves;
        }

        private void addKingMoves(ChessPosition position) {
            validateAndAddMoves(position, new ChessPosition(position.getRow() + 1, position.getColumn() + 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() + 1, position.getColumn() - 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() + 1, position.getColumn()));
            validateAndAddMoves(position, new ChessPosition(position.getRow() - 1, position.getColumn() + 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() - 1, position.getColumn() - 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() - 1, position.getColumn()));
            validateAndAddMoves(position, new ChessPosition(position.getRow(), position.getColumn() + 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow(), position.getColumn() - 1));
        }

        private void addKnightMoves(ChessPosition position) {
            validateAndAddMoves(position, new ChessPosition(position.getRow() + 2, position.getColumn() + 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() + 2, position.getColumn() - 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() + 1, position.getColumn() + 2));
            validateAndAddMoves(position, new ChessPosition(position.getRow() - 1, position.getColumn() + 2));
            validateAndAddMoves(position, new ChessPosition(position.getRow() - 2, position.getColumn() - 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() - 2, position.getColumn() + 1));
            validateAndAddMoves(position, new ChessPosition(position.getRow() + 1, position.getColumn() - 2));
            validateAndAddMoves(position, new ChessPosition(position.getRow() - 1, position.getColumn() - 2));
        }

        private void addPawnMoves(ChessPosition position) {
            if (teamColor == ChessGame.TeamColor.WHITE) {
                handlePawnMoves(position, 1);
            } else {
                handlePawnMoves(position, -1);
            }
        }

        private void handlePawnMoves(ChessPosition position, int direction) {
            ChessPosition oneStep = new ChessPosition(position.getRow() + direction, position.getColumn());
            ChessPosition twoStep = new ChessPosition(position.getRow() + 2 * direction, position.getColumn());
            ChessPosition attackRight = new ChessPosition(position.getRow() + direction, position.getColumn() + 1);
            ChessPosition attackLeft = new ChessPosition(position.getRow() + direction, position.getColumn() - 1);

            if (checkSpace(oneStep) == Space.OPEN) {
                pawnAddAndPromote(position, oneStep);
                if ((direction == 1 && position.getRow() == 2) || (direction == -1 && position.getRow() == 7)) {
                    if (checkSpace(twoStep) == Space.OPEN) {
                        pawnAddAndPromote(position, twoStep);
                    }
                }
            }

            if (isOnBoard(attackRight) && checkSpace(attackRight) == Space.CAPTURE) {
                pawnAddAndPromote(position, attackRight);
            }

            if (isOnBoard(attackLeft) && checkSpace(attackLeft) == Space.CAPTURE) {
                pawnAddAndPromote(position, attackLeft);
            }
        }

        private void pawnAddAndPromote(ChessPosition start, ChessPosition end) {
            if (isOnBoard(end)) {
                if (teamColor == ChessGame.TeamColor.WHITE && end.getRow() == 8) {
                    addPromotionMoves(start, end);
                } else if (teamColor == ChessGame.TeamColor.BLACK && end.getRow() == 1) {
                    addPromotionMoves(start, end);
                } else {
                    moves.add(new ChessMove(start, end, null));
                }
            }
        }

        private void addPromotionMoves(ChessPosition start, ChessPosition end) {
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
        }


        private void validateAndAddMoves(ChessPosition position, ChessPosition endPosition) {
            if (isOnBoard(endPosition) && (checkSpace(endPosition) == Space.OPEN || checkSpace(endPosition) == Space.CAPTURE)) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }


        private boolean isOnBoard(ChessPosition position) {
            return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
        }

        private enum Space {
            OPEN,
            CAPTURE,
            SAME_TEAM
        }


        private Space checkSpace(ChessPosition endPosition) {
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null) {
                return Space.OPEN;
            } else if (piece.getTeamColor() != teamColor) {
                return Space.CAPTURE;
            } else {
                return Space.SAME_TEAM;
            }
        }

        private void moveInDirections(ChessPosition start, int rowDirection, int columnDirection) {
            ChessPosition nextStep = new ChessPosition(start.getRow() + rowDirection, start.getColumn() + columnDirection);
            while (isOnBoard(nextStep)) {
                Space space = checkSpace(nextStep);
                if (space == Space.OPEN) {
                    moves.add(new ChessMove(start, nextStep, null));
                } else if (space == Space.CAPTURE) {
                    moves.add(new ChessMove(start, nextStep, null));
                    break;
                } else {
                    break;
                }
                nextStep = new ChessPosition(nextStep.getRow() + rowDirection, nextStep.getColumn() + columnDirection);
            }
        }
    }
}
