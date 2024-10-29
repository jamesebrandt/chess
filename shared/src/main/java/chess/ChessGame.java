package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Stack;

public class ChessGame {
    private ChessBoard board;
    private TeamColor teamColor;
    private Stack<MoveHistory> tempMoveHistory;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.tempMoveHistory = new Stack<>();
        this.teamColor = TeamColor.WHITE;
    }

    public TeamColor getTeamTurn() {
        return teamColor;
    }

    public void setTeamTurn(TeamColor team) {
        this.teamColor = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        }
        Collection<ChessMove> testMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> movesToRemove = new ArrayList<>();
        TeamColor color = piece.getTeamColor();

        for (ChessMove move : testMoves) {
            makeTempMove(piece, move.getStartPosition(), move.getEndPosition());
            if (isInCheck(color)) {
                movesToRemove.add(move);
            }
            undoTempMove(piece, move.getStartPosition(), move.getEndPosition());
        }
        testMoves.removeAll(movesToRemove);
        return testMoves;
    }

    private ChessPosition findKing(TeamColor team) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition checkingPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(checkingPosition);
                if (isKingOfTeam(piece, team)) {
                    return checkingPosition;
                }
            }
        }
        return null;
    }

    private boolean isKingOfTeam(ChessPiece piece, TeamColor team) {
        return piece != null &&
                piece.getPieceType().equals(ChessPiece.PieceType.KING) &&
                piece.getTeamColor().equals(team);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        validateMove(piece, move);
        executeMove(piece, move);
        updateGameState();
    }

    private void validateMove(ChessPiece piece, ChessMove move) throws InvalidMoveException {
        if (piece == null || piece.getTeamColor() != teamColor) {
            throw new InvalidMoveException("Invalid Move: There is no piece at that start position");
        }
        Collection<ChessMove> validMoves = piece.pieceMoves(board, move.getStartPosition());
        if (!validMoves.contains(move) || putsInCheck(move.getStartPosition())) {
            throw new InvalidMoveException("Invalid Move: That was not a valid move");
        }
    }

    private void executeMove(ChessPiece piece, ChessMove move) {
        if (shouldPromotePawn(piece, move)) {
            ChessPiece promotedPiece = new ChessPiece(teamColor, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPiece);
        } else {
            board.addPiece(move.getEndPosition(), piece);
        }
        board.addPiece(move.getStartPosition(), null);
    }

    private boolean shouldPromotePawn(ChessPiece piece, ChessMove move) {
        return piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                move.getPromotionPiece() != null &&
                (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8);
    }

    private void updateGameState() throws InvalidMoveException {
        TeamColor opposingTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        if (isInCheck(opposingTeam)) {
            setTeamTurn(opposingTeam);
            return;
        }
        if (isInCheck(teamColor)) {
            throw new InvalidMoveException("Invalid Move: You must move to get out of check.");
        }
        setTeamTurn(opposingTeam);
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }
        return isPositionUnderAttack(kingPosition, teamColor);
    }

    private boolean isPositionUnderAttack(ChessPosition position, TeamColor defendingTeam) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition attackingPosition = new ChessPosition(row, col);
                ChessPiece attacker = board.getPiece(attackingPosition);
                if (canPieceAttackPosition(attacker, attackingPosition, position, defendingTeam)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canPieceAttackPosition(ChessPiece piece, ChessPosition from,
                                           ChessPosition to, TeamColor defendingTeam) {
        if (piece == null || piece.getTeamColor() == defendingTeam) {
            return false;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, from);
        return moves.stream().anyMatch(move -> move.getEndPosition().equals(to));
    }

    private boolean putsInCheck(ChessPosition position) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null || !kingPosition.equals(position)) {
            return false;
        }
        return isPositionUnderAttack(position, teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return !hasValidMoves(teamColor);
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (canPieceMakeValidMove(piece, position, teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canPieceMakeValidMove(ChessPiece piece, ChessPosition position,
                                          TeamColor teamColor) {
        if (piece == null || piece.getTeamColor() != teamColor) {
            return false;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        for (ChessMove move : moves) {
            makeTempMove(piece, move.getStartPosition(), move.getEndPosition());
            boolean inCheck = isInCheck(teamColor);
            undoTempMove(piece, move.getStartPosition(), move.getEndPosition());
            if (!inCheck) {
                return true;
            }
        }
        return false;
    }

    private void makeTempMove(ChessPiece piece, ChessPosition startPosition, ChessPosition end) {
        ChessPiece capturedPiece = board.getPiece(end);
        board.addPiece(end, piece);
        board.addPiece(startPosition, null);
        tempMoveHistory.push(new MoveHistory(startPosition, end, piece, capturedPiece));
    }

    private void undoTempMove(ChessPiece piece, ChessPosition position, ChessPosition end) {
        if (tempMoveHistory.isEmpty()) {
            return;
        }
        MoveHistory lastMove = tempMoveHistory.pop();
        board.addPiece(lastMove.getStartPosition(), lastMove.getMovedPiece());
        board.addPiece(lastMove.getEndPosition(), lastMove.capturedPiece);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return !hasValidMoves(teamColor) && hasPieces(teamColor);
    }

    private boolean hasPieces(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public class MoveHistory {
        private ChessPosition startPosition;
        private ChessPosition endPosition;
        private ChessPiece movedPiece;
        private ChessPiece capturedPiece;

        public MoveHistory(ChessPosition startPosition, ChessPosition endPosition,
                           ChessPiece movedPiece, ChessPiece capturedPiece) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.movedPiece = movedPiece;
            this.capturedPiece = capturedPiece;
        }

        public ChessPosition getStartPosition() { return startPosition; }
        public ChessPosition getEndPosition() { return endPosition; }
        public ChessPiece getMovedPiece() { return movedPiece; }
        public ChessPiece getCapturedPiece() { return capturedPiece; }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true};
        if (o == null || getClass() != o.getClass()) {return false};
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) &&
                teamColor == chessGame.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), teamColor);
    }
}