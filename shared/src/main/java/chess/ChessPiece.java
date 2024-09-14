package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
            return null;
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return null;
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
            return null;
        }

        //BISHOP MOVE SECTION
        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            for (int i = 1; i < 8; i++) {
                ChessPosition newPosition = new ChessPosition(i + myPosition.getRow(),i + myPosition.getColumn());
                if (newPosition.getRow() < 9 || newPosition.getColumn() < 9) {
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                }
                else if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                    break;
                }
                else{
                    break;
                }
            }
            for (int i = 1; i < 8; i++) {
                ChessPosition newPosition = new ChessPosition(i + myPosition.getRow(),i - myPosition.getColumn());
                if (newPosition.getRow() < 9 || newPosition.getColumn() > -1) {
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                }
                else if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                    break;
                }
                else{
                    break;
                }
            }
            for (int i = 1; i < 8; i++) {
                ChessPosition newPosition = new ChessPosition(i - myPosition.getRow(),i - myPosition.getColumn());
                if (newPosition.getRow() > -1 || newPosition.getColumn() > -1) {
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                }
                else if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                    break;
                }
                else{
                    break;
                }
            }
            for (int i = 1; i < 8; i++) {
                ChessPosition newPosition = new ChessPosition(i - myPosition.getRow(),i + myPosition.getColumn());
                if (newPosition.getRow() > -1 || newPosition.getColumn() < 9) {
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                }
                else if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                    ChessMove move = new ChessMove(myPosition,newPosition,null);
                    movesList.add(move);
                    break;
                }
                else{
                    break;
                }
            }
        }

        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return null;
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return null;
        }
        else {
            throw new RuntimeException("Not implemented");
        }
        return movesList;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
