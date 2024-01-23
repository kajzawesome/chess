package chess;

import java.util.ArrayList;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;
import static chess.ChessPiece.PieceType.PAWN;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ArrayList<ChessMove> FindMoves(ChessBoard board, ChessPiece piece, ChessPosition startPosition){
        int currentRow = startPosition.getRow();
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
        if (piece.getPieceType() == PAWN && piece.getTeamColor() == WHITE){
            ChessPosition position1 = new ChessPosition(currentRow+1,startPosition.getColumn());
            if(board.getPiece(position1) == null) {
                ChessMove move = new ChessMove(startPosition, position1, null);
                possibleMoves.add(move);
            }
            if (currentRow == 1){
                ChessPosition position2 = new ChessPosition(currentRow+2,startPosition.getColumn());
                if(board.getPiece(position2) == null){
                    ChessMove move = new ChessMove(startPosition, position2, null);
                    possibleMoves.add(move);
                }
            }
            ChessPosition diagonal1 = new ChessPosition(currentRow+1,startPosition.getColumn()-1);
            if(board.getPiece(diagonal1) != null && board.getPiece(position1).getTeamColor() != WHITE) {
                ChessMove move = new ChessMove(startPosition, diagonal1, PAWN);
                possibleMoves.add(move);
            }
            ChessPosition diagonal2 = new ChessPosition(currentRow+1,startPosition.getColumn()+1);
            if(board.getPiece(diagonal2) != null && board.getPiece(position1).getTeamColor() != WHITE) {
                ChessMove move = new ChessMove(startPosition, diagonal2, PAWN);
                possibleMoves.add(move);
            }
        }
        if (piece.getPieceType() == PAWN && piece.getTeamColor() == BLACK){
            ChessPosition position1 = new ChessPosition(currentRow-1,startPosition.getColumn());
            if(board.getPiece(position1) == null) {
                ChessMove move = new ChessMove(startPosition, position1, null);
                possibleMoves.add(move);
            }
            if (currentRow == 6){
                ChessPosition position2 = new ChessPosition(currentRow-2,startPosition.getColumn());
                if(board.getPiece(position2) == null){
                    ChessMove move = new ChessMove(startPosition, position2, null);
                    possibleMoves.add(move);
                }
            }
            ChessPosition diagonal1 = new ChessPosition(currentRow-1,startPosition.getColumn()-1);
            if(board.getPiece(diagonal1) != null && board.getPiece(position1).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, diagonal1, PAWN);
                possibleMoves.add(move);
            }
            ChessPosition diagonal2 = new ChessPosition(currentRow-1,startPosition.getColumn()+1);
            if(board.getPiece(diagonal2) != null && board.getPiece(position1).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, diagonal2, PAWN);
                possibleMoves.add(move);
            }
        }

        if (piece.getPieceType() == KING) {
            ChessPosition left = new ChessPosition(startPosition.getRow(), startPosition.getColumn());
            ChessPosition right = new ChessPosition(startPosition.getRow(), startPosition.getColumn());
            ChessPosition up = new ChessPosition(startPosition.getRow(), startPosition.getColumn());
            ChessPosition down = new ChessPosition(startPosition.getRow(), startPosition.getColumn());
            ChessPosition diagonal1 = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() + 1);
            ChessPosition diagonal2 = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() + 1);
            ChessPosition diagonal3 = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() - 1);
            ChessPosition diagonal4 = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() - 1);
            if (board.getPiece(left).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, left, KING);
                possibleMoves.add(move);
            }
            if (board.getPiece(right).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, right, KING);
                possibleMoves.add(move);
            }
            if (board.getPiece(up).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, up, KING);
                possibleMoves.add(move);
            }
            if (board.getPiece(down).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, down, KING);
                possibleMoves.add(move);
            }
            if (board.getPiece(diagonal1).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, diagonal1, KING);
                possibleMoves.add(move);
            }
            if (board.getPiece(diagonal2).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, diagonal2, KING);
                possibleMoves.add(move);
            }
            if (board.getPiece(diagonal3).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, diagonal3, KING);
                possibleMoves.add(move);
            }
            if (board.getPiece(diagonal4).getTeamColor() != piece.getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, diagonal4, KING);
                possibleMoves.add(move);
            }
        }
        return possibleMoves;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}


