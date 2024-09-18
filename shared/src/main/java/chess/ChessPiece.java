package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
            movesList.addAll(straightUp(board, myPosition));
            movesList.addAll(straightDown(board, myPosition));
            movesList.addAll(straightRight(board, myPosition));
            movesList.addAll(straightLeft(board, myPosition));
        }

        //BISHOP MOVE SECTION
        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            movesList.addAll(upRight(board, myPosition));
            movesList.addAll(downRight(board, myPosition));
            movesList.addAll(downLeft(board, myPosition));
            movesList.addAll(upLeft(board, myPosition));
        }

        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            movesList.addAll(upRight(board, myPosition));
            movesList.addAll(upLeft(board, myPosition));
            movesList.addAll(downRight(board, myPosition));
            movesList.addAll(downLeft(board, myPosition));
            movesList.addAll(straightUp(board, myPosition));
            movesList.addAll(straightDown(board, myPosition));
            movesList.addAll(straightRight(board, myPosition));
            movesList.addAll(straightLeft(board, myPosition));
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return null;
        }
        else {
            throw new RuntimeException("Not implemented");
        }
        return movesList;
    }

    public Collection<ChessMove> straightUp(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(i + myPosition.getRow(), myPosition.getColumn());
            if ((newPosition.getRow() < 9) && (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
            } else if ((newPosition.getRow() < 9) && (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor())) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
                break;
            } else {
                break;
            }
        }
        return movesList;
    }

    public Collection<ChessMove> straightDown(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn());
            if ((newPosition.getRow() > 0) && (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
            } else if ((newPosition.getRow() > 0) && (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor())) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
                break;
            } else {
                break;
            }

        }
        return movesList;
    }

    public Collection<ChessMove> straightRight(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), i + myPosition.getColumn());
            if ((newPosition.getColumn() < 9) && (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
            } else if ((newPosition.getColumn() < 9) && (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor())) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
                break;
            } else {
                break;
            }

        }
        return movesList;
    }

    public Collection<ChessMove> straightLeft(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);
            if ((newPosition.getColumn() > 0) && (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
            } else if ((newPosition.getColumn() > 0) && (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor())) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
                break;
            } else {
                break;
            }

        }
        return movesList;
    }

    public Collection<ChessMove> upRight(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(i + myPosition.getRow(), i + myPosition.getColumn());
            if ((newPosition.getRow() < 9 && newPosition.getColumn() < 9) && (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
            } else if ((newPosition.getRow() < 9 && newPosition.getColumn() < 9) && (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor())) {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                movesList.add(move);
                break;
            } else {
                break;
            }

        }
        return movesList;
    }

    public Collection<ChessMove> upLeft(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(i + myPosition.getRow(),myPosition.getColumn() - i);
            if ((newPosition.getRow() < 9 && newPosition.getColumn() > 0) &&  (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition,newPosition,null);
                movesList.add(move);
            }
            else if((newPosition.getRow() < 9 && newPosition.getColumn() > 0) && (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor())) {
                ChessMove move = new ChessMove(myPosition,newPosition,null);
                movesList.add(move);
                break;
            }
            else{
                break;
            }
        }
        return movesList;
    }

    public Collection<ChessMove> downRight(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - i,i + myPosition.getColumn());
            if ((newPosition.getRow() > 0 && newPosition.getColumn() < 9) &&  (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition,newPosition,null);
                movesList.add(move);
            }
            else if((newPosition.getRow() > 0 && newPosition.getColumn() < 9) && board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                ChessMove move = new ChessMove(myPosition,newPosition,null);
                movesList.add(move);
                break;
            }
            else{
                break;
            }
        }
        return movesList;
    }

    public Collection<ChessMove> downLeft(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> movesList = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() - i,myPosition.getColumn() - i);
            if ((newPosition.getRow() > 0 && newPosition.getColumn() > 0) &&  (board.getPiece(newPosition) == null)) {
                ChessMove move = new ChessMove(myPosition,newPosition,null);
                movesList.add(move);
            }
            else if((newPosition.getRow() > 0 && newPosition.getColumn() > 0) && board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                ChessMove move = new ChessMove(myPosition,newPosition,null);
                movesList.add(move);
                break;
            }
            else{
                break;
            }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
