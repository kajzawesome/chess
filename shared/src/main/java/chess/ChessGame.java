package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamColor;
    ChessBoard currentBoard = new ChessBoard();

    public ChessGame() {
        setBoard(currentBoard);
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        if (currentBoard.getPiece(startPosition) != null) {
            if (isInCheck(currentBoard.getPiece(startPosition).getTeamColor())) {
                if (isInCheckmate(currentBoard.getPiece(startPosition).getTeamColor())) {
                    return null;
                }
                ChessPiece currPiece = getBoard().getPiece(startPosition);
                possibilities = currPiece.pieceMoves(getBoard(), startPosition);
                ChessBoard possibleBoard = currentBoard;
                if (!possibilities.isEmpty()) {
                    for (ChessMove move : possibilities) {
                        currentBoard = possibleBoard;
                        currentBoard.addPiece(move.getStartPosition(), null);
                        currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                        if (isInCheck(possibleBoard.getPiece(startPosition).getTeamColor())) {
                            if (isInCheckmate(possibleBoard.getPiece(startPosition).getTeamColor())) {
                                possibilities.remove(move);
                            }
                            possibilities.remove(move);
                        }
                    }
                }
                currentBoard = possibleBoard;
            }
            else if (!isInStalemate(currentBoard.getPiece(startPosition).getTeamColor())){
                ChessPiece currPiece = getBoard().getPiece(startPosition);
                possibilities = currPiece.pieceMoves(getBoard(), startPosition);
                ChessBoard possibleBoard = currentBoard;
                if (!possibilities.isEmpty()) {
                    for (ChessMove move : possibilities) {
                        currentBoard = possibleBoard;
                        currentBoard.addPiece(move.getStartPosition(), null);
                        currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                        if (isInCheck(possibleBoard.getPiece(startPosition).getTeamColor())) {
                            if (isInCheckmate(possibleBoard.getPiece(startPosition).getTeamColor())) {
                                possibilities.remove(move);
                            }
                            possibilities.remove(move);
                        }
                    }
                }
                currentBoard = possibleBoard;
                return possibilities;
            }
        }
        return possibilities;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> movesList = validMoves(move.getStartPosition());
        if (!movesList.isEmpty()) {
            if (movesList.contains(move)) {
                currentBoard.addPiece(move.getStartPosition(), null);
                currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
            }
        }
        else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (currentBoard.getPiece(new ChessPosition(i, j)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING && currentBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                        kingPosition = new ChessPosition(i, j);
                        break;
                    }
                }
            }
            if (kingPosition != null) {
                break;
            }
        }
        assert kingPosition != null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                ChessPiece currPiece = currentBoard.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> possibilities = currPiece.pieceMoves(getBoard(), currPosition);
                    ChessMove possibleMove = new ChessMove(currPosition, kingPosition, null);
                    if (!possibilities.isEmpty()) {
                        if (currPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                            ChessMove promoMove1 = new ChessMove(currPosition, kingPosition, ChessPiece.PieceType.QUEEN);
                            ChessMove promoMove2 = new ChessMove(currPosition, kingPosition, ChessPiece.PieceType.ROOK);
                            ChessMove promoMove3 = new ChessMove(currPosition, kingPosition, ChessPiece.PieceType.BISHOP);
                            ChessMove promoMove4 = new ChessMove(currPosition, kingPosition, ChessPiece.PieceType.KNIGHT);
                            if (possibilities.contains(possibleMove)) {
                                return true;
                            } else if (possibilities.contains(promoMove1)) {
                                return true;
                            } else if (possibilities.contains(promoMove2)) {
                                return true;
                            } else if (possibilities.contains(promoMove3)) {
                                return true;
                            } else if (possibilities.contains(promoMove4)) {
                                return true;
                            }
                        }
                        else if (possibilities.contains(possibleMove)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (currentBoard.getPiece(new ChessPosition(i,j)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING && currentBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                        kingPosition = new ChessPosition(i, j);
                        break;
                    }
                }
            }
            if (kingPosition != null) {
                break;
            }
        }
        assert kingPosition != null;
        ChessPiece kingPiece = currentBoard.getPiece(kingPosition);
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(getBoard(), kingPosition);
        ChessBoard possibleBoard = currentBoard;
        if (!kingMoves.isEmpty()) {
            for (ChessMove move : kingMoves) {
                currentBoard = possibleBoard;
                currentBoard.addPiece(move.getStartPosition(), null);
                currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                if (!isInCheck(teamColor)) {
                    currentBoard = possibleBoard;
                    return false;
                }
            }
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                ChessPiece currPiece = currentBoard.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = currPiece.pieceMoves(getBoard(), currPosition);
                    if (!possibleMoves.isEmpty()) {
                        for (ChessMove move : possibleMoves) {
                            currentBoard = possibleBoard;
                            currentBoard.addPiece(move.getStartPosition(), null);
                            currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                            if (!isInCheck(teamColor)) {
                                currentBoard = possibleBoard;
                                return false;
                            }
                        }
                    }
                }
            }
        }
        currentBoard = possibleBoard;
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                ChessPiece currPiece = currentBoard.getPiece(currPosition);
                if (currPiece != null) {
                    if (currPiece.getPieceType() == ChessPiece.PieceType.KING && currPiece.getTeamColor() == teamColor) {
                        Collection<ChessMove> possibleMoves = currPiece.pieceMoves(getBoard(), currPosition);
                        if (!possibleMoves.isEmpty()) {
                            ChessBoard possibleBoard = currentBoard;
                            for (ChessMove move : possibleMoves) {
                                currentBoard = possibleBoard;
                                currentBoard.addPiece(move.getStartPosition(), null);
                                currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                                if (!isInCheck(teamColor)) {
                                    currentBoard = possibleBoard;
                                    return false;
                                }
                            }
                            currentBoard = possibleBoard;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }


}
