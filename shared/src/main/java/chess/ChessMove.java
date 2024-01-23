package chess;

import java.util.ArrayList;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
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

    public ArrayList<ChessPosition> FindMoves(ChessBoard board, ChessPiece piece, ChessPosition startPosition){
        int currentRow = startPosition.getRow();
        ArrayList<ChessPosition> possibleMoves = new ArrayList<ChessPosition>();
        if (piece.getPieceType() == PAWN && piece.getTeamColor() == WHITE){
            ChessPosition position1 = new ChessPosition(currentRow+1,startPosition.getColumn());
            if(board.getPiece(position1) == null) {
                possibleMoves.add(position1);
            }
            if (currentRow == 1){
                ChessPosition position2 = new ChessPosition(currentRow+2,startPosition.getColumn());
                if(board.getPiece(position2) == null){
                    possibleMoves.add(position2);
                }
            }
        }
        if (piece.getPieceType() == PAWN && piece.getTeamColor() == BLACK){
            ChessPosition position1 = new ChessPosition(currentRow-1,startPosition.getColumn());
            if(board.getPiece(position1) == null) {
                possibleMoves.add(position1);
            }
            if (currentRow == 6){
                ChessPosition position2 = new ChessPosition(currentRow-2,startPosition.getColumn());
                if(board.getPiece(position2) == null){
                    possibleMoves.add(position2);
                }
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
