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
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                if (myPosition.getRow() == 7) {
                    ChessPosition downDown = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                    ChessPosition down = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                    if (board.getPiece(down) == null) {
                        ChessMove secondMove = new ChessMove(myPosition, down, null);
                        movesList.add(secondMove);
                        if (board.getPiece(downDown) == null) {
                            ChessMove firstMove = new ChessMove(myPosition, downDown, null);
                            movesList.add(firstMove);
                        }
                    }
                }
                else {
                    ChessPosition downRight = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()+1);
                    ChessPosition downLeft = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()-1);
                    ChessPosition down = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn());
                    if (down.getRow() > 0 && board.getPiece(down) == null) {
                        ChessMove downMove;
                        if (down.getRow() == 1) {
                            downMove = new ChessMove(myPosition, down, PieceType.QUEEN);
                        }
                        else {
                            downMove = new ChessMove(myPosition, down, null);
                        }
                        movesList.add(downMove);
                    }
                    if (downRight.getRow() > 0 && board.getPiece(downRight) != null && board.getPiece(downRight).getTeamColor() != ChessGame.TeamColor.BLACK) {
                        ChessMove downRightMove;
                        if (downRight.getRow() == 1) {
                            downRightMove = new ChessMove(myPosition, downRight, PieceType.QUEEN);
                        }
                        else {
                            downRightMove = new ChessMove(myPosition, downRight, null);
                        }
                        movesList.add(downRightMove);
                    }
                    if (downLeft.getRow() > 0 && board.getPiece(downLeft) != null && board.getPiece(downLeft).getTeamColor() != ChessGame.TeamColor.BLACK) {
                        ChessMove downLeftMove;
                        if (downLeft.getRow() == 1) {
                            downLeftMove = new ChessMove(myPosition, downLeft, PieceType.QUEEN);
                        }
                        else {
                            downLeftMove = new ChessMove(myPosition, downLeft, null);
                        }
                        movesList.add(downLeftMove);
                    }
                }

            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (myPosition.getRow() == 2) {
                    ChessPosition upUp = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                    ChessPosition up = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                    if (board.getPiece(up) == null) {
                        ChessMove secondMove = new ChessMove(myPosition, up, null);
                        movesList.add(secondMove);
                        if (board.getPiece(upUp) == null) {
                            ChessMove firstMove = new ChessMove(myPosition, upUp, null);
                            movesList.add(firstMove);
                        }
                    }
                }
                else {
                    ChessPosition upRight = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()+1);
                    ChessPosition upLeft = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()-1);
                    ChessPosition up = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn());
                    if (up.getRow() < 9 && board.getPiece(up) == null) {
                        ChessMove upMove;
                        if (up.getRow() == 8) {
                            upMove = new ChessMove(myPosition, up, PieceType.QUEEN);
                        }
                        else {
                            upMove = new ChessMove(myPosition, up, null);
                        }
                        movesList.add(upMove);
                    }
                    if (upRight.getRow() < 9 && board.getPiece(upRight) != null && board.getPiece(upRight).getTeamColor() != ChessGame.TeamColor.WHITE) {
                        ChessMove upRightMove;
                        if (upRight.getRow() == 8) {
                            upRightMove = new ChessMove(myPosition, upRight, PieceType.QUEEN);
                        }
                        else {
                            upRightMove = new ChessMove(myPosition, upRight, null);
                        }
                        movesList.add(upRightMove);
                    }
                    if (upLeft.getRow() < 9 && board.getPiece(upLeft) != null && board.getPiece(upLeft).getTeamColor() != ChessGame.TeamColor.WHITE) {
                        ChessMove upLeftMove;
                        if (upLeft.getRow() == 8) {
                            upLeftMove = new ChessMove(myPosition, upLeft, PieceType.QUEEN);
                        }
                        else {
                            upLeftMove = new ChessMove(myPosition, upLeft, null);
                        }
                        movesList.add(upLeftMove);
                    }
                }
            }
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            ChessPosition positionUpRight = new ChessPosition(myPosition.getRow()+2,myPosition.getColumn()+1);
            if ((positionUpRight.getRow() < 9) && (positionUpRight.getColumn() < 9)) {
                if ((board.getPiece(positionUpRight) == null) || ((board.getPiece(positionUpRight).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionUpRightMove = new ChessMove(myPosition, positionUpRight, null);
                    movesList.add(positionUpRightMove);
                }
            }
            ChessPosition positionUpLeft = new ChessPosition(myPosition.getRow()+2,myPosition.getColumn()-1);
            if ((positionUpLeft.getRow() < 9) && (positionUpLeft.getColumn() > 0)) {
                if ((board.getPiece(positionUpLeft) == null) || ((board.getPiece(positionUpLeft).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionUpLeftMove = new ChessMove(myPosition, positionUpLeft, null);
                    movesList.add(positionUpLeftMove);
                }
            }
            ChessPosition positionDownRight = new ChessPosition(myPosition.getRow()-2,myPosition.getColumn()+1);
            if ((positionDownRight.getRow() > 0) && (positionDownRight.getColumn() < 9)) {
                if ((board.getPiece(positionDownRight) == null) || ((board.getPiece(positionDownRight).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionDownRightMove = new ChessMove(myPosition, positionDownRight, null);
                    movesList.add(positionDownRightMove);
                }
            }
            ChessPosition positionDownLeft = new ChessPosition(myPosition.getRow()-2,myPosition.getColumn()-1);
            if ((positionDownLeft.getRow() > 0) && (positionDownLeft.getColumn() > 0)) {
                if ((board.getPiece(positionDownLeft) == null) || ((board.getPiece(positionDownLeft).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionDownLeftMove = new ChessMove(myPosition, positionDownLeft, null);
                    movesList.add(positionDownLeftMove);
                }
            }
            ChessPosition positionRightUp = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()+2);
            if ((positionRightUp.getRow() < 9) && (positionRightUp.getColumn() < 9)) {
                if ((board.getPiece(positionRightUp) == null) || ((board.getPiece(positionRightUp).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionRightUpMove = new ChessMove(myPosition, positionRightUp, null);
                    movesList.add(positionRightUpMove);
                }
            }
            ChessPosition positionRightDown = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()+2);
            if ((positionRightDown.getRow() > 0) && (positionRightDown.getColumn() < 9)) {
                if ((board.getPiece(positionRightDown) == null) || ((board.getPiece(positionRightDown).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionRightDownMove = new ChessMove(myPosition, positionRightDown, null);
                    movesList.add(positionRightDownMove);
                }
            }
            ChessPosition positionLeftUp = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()-2);
            if ((positionLeftUp.getRow() < 9) && (positionLeftUp.getColumn() > 0)) {
                if ((board.getPiece(positionLeftUp) == null) || ((board.getPiece(positionLeftUp).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionLeftUpMove = new ChessMove(myPosition, positionLeftUp, null);
                    movesList.add(positionLeftUpMove);
                }
            }
            ChessPosition positionLeftDown = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()-2);
            if ((positionLeftDown.getRow() > 0) && (positionLeftDown.getColumn() > 0)) {
                if ((board.getPiece(positionLeftDown) == null) || ((board.getPiece(positionLeftDown).getTeamColor()) != board.getPiece(myPosition).getTeamColor())) {
                    ChessMove positionLeftDownMove = new ChessMove(myPosition, positionLeftDown, null);
                    movesList.add(positionLeftDownMove);
                }
            }
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
            ChessPosition positionUp = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn());
            if (positionUp.getRow() < 9) {
                if ((board.getPiece(positionUp) == null) ||(board.getPiece(positionUp).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionUpMove = new ChessMove(myPosition, positionUp, null);
                    movesList.add(positionUpMove);
                }
            }
            ChessPosition positionDown = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn());
            if (positionDown.getRow() > 0) {
                if ((board.getPiece(positionDown) == null) || (board.getPiece(positionDown).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionDownMove = new ChessMove(myPosition, positionDown, null);
                    movesList.add(positionDownMove);
                }
            }
            ChessPosition positionRight = new ChessPosition(myPosition.getRow(),myPosition.getColumn()+1);
            if (positionRight.getColumn() < 9) {
                if ((board.getPiece(positionRight) == null) ||(board.getPiece(positionRight).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionRightMove = new ChessMove(myPosition, positionRight, null);
                    movesList.add(positionRightMove);
                }
            }
            ChessPosition positionLeft = new ChessPosition(myPosition.getRow(),myPosition.getColumn()-1);
            if (positionLeft.getColumn() > 0) {
                if ((board.getPiece(positionLeft) == null) ||(board.getPiece(positionLeft).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionLeftMove = new ChessMove(myPosition, positionLeft, null);
                    movesList.add(positionLeftMove);
                }
            }
            ChessPosition positionUpRight = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()+1);
            if ((positionUpRight.getRow() < 9) && (positionUpRight.getColumn() < 9)) {
                if ((board.getPiece(positionUpRight) == null) || (board.getPiece(positionUpRight).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionUpRightMove = new ChessMove(myPosition, positionUpRight, null);
                    movesList.add(positionUpRightMove);
                }
            }
            ChessPosition positionDownRight = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()+1);
            if ((positionDownRight.getRow() > 0) && (positionDownRight.getColumn() < 9)) {
                if ((board.getPiece(positionDownRight) == null) || (board.getPiece(positionDownRight).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionDownRightMove = new ChessMove(myPosition, positionDownRight, null);
                    movesList.add(positionDownRightMove);
                }
            }
            ChessPosition positionUpLeft = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()-1);
            if ((positionUpLeft.getRow()) < 9 && (positionUpLeft.getColumn() > 0)) {
                if ((board.getPiece(positionUpLeft) == null) || (board.getPiece(positionUpLeft).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionUpLeftMove = new ChessMove(myPosition, positionUpLeft, null);
                    movesList.add(positionUpLeftMove);
                }
            }
            ChessPosition positionDownLeft = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()-1);
            if ((positionDownLeft.getRow() > 0) && (positionDownLeft.getColumn() > 0)) {
                if ((board.getPiece(positionDownLeft) == null) || (board.getPiece(positionDownLeft).getTeamColor()) != board.getPiece(myPosition).getTeamColor()) {
                    ChessMove positionDownLeftMove = new ChessMove(myPosition, positionDownLeft, null);
                    movesList.add(positionDownLeftMove);
                }
            }
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
