package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDataAccessSQL;
import dataaccess.GameDataAccessSQL;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    /** A rule violation that gets reported back to the sender as an ERROR message. */
    private static class GameplayException extends Exception {
        GameplayException(String message) {
            super(message);
        }
    }

    private final Gson gson = new Gson();
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDataAccessSQL authData;
    private final GameDataAccessSQL gameData;

    public WebSocketHandler(AuthDataAccessSQL authData, GameDataAccessSQL gameData) {
        this.authData = authData;
        this.gameData = gameData;
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.removeEverywhere(session);
    }

    // synchronized so two players' commands can't change the same game at the same time
    @OnWebSocketMessage
    public synchronized void onMessage(Session session, String text) throws IOException {
        try {
            var command = gson.fromJson(text, UserGameCommand.class);
            if (command == null || command.getCommandType() == null) {
                throw new GameplayException("unknown command");
            }
            String username = authenticate(command.getAuthToken());
            GameData game = loadGame(command.getGameID());
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, game);
                case MAKE_MOVE -> makeMove(session, username, game,
                        gson.fromJson(text, MakeMoveCommand.class).getMove());
                case LEAVE -> leave(session, username, game);
                case RESIGN -> resign(session, username, game);
            }
        } catch (Exception ex) {
            connections.send(session, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void connect(Session session, String username, GameData game) throws IOException {
        connections.add(game.gameID(), session, username);
        connections.send(session, new LoadGameMessage(game.game()));

        var color = colorOf(username, game);
        String note = (color == null)
                ? String.format("%s is now observing the game", username)
                : String.format("%s joined the game as %s", username, color);
        connections.broadcast(game.gameID(), session, new NotificationMessage(note));
    }

    private void makeMove(Session session, String username, GameData game, ChessMove move) throws Exception {
        ChessGame chess = game.game();
        if (chess.isGameOver()) {
            throw new GameplayException("the game is over, no more moves can be made");
        }
        var color = colorOf(username, game);
        if (color == null) {
            throw new GameplayException("observers can't make moves");
        }
        if (chess.getTeamTurn() != color) {
            throw new GameplayException("it's not your turn");
        }
        if (move == null) {
            throw new GameplayException("no move given");
        }
        var piece = chess.getBoard().getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != color) {
            throw new GameplayException("you don't have a piece on " + square(move.getStartPosition()));
        }
        try {
            chess.makeMove(move);
        } catch (InvalidMoveException ex) {
            throw new GameplayException("that move isn't allowed");
        }

        // See if the move ended the game or put the opponent in check
        var opponent = (color == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String opponentName = playerName(opponent, game);
        String status = null;
        if (chess.isInCheckmate(opponent)) {
            chess.setGameOver(true);
            status = String.format("%s (%s) is in checkmate. %s wins!", opponentName, opponent, username);
        } else if (chess.isInStalemate(opponent)) {
            chess.setGameOver(true);
            status = String.format("%s (%s) has no legal moves. The game is a stalemate.", opponentName, opponent);
        } else if (chess.isInCheck(opponent)) {
            status = String.format("%s (%s) is in check", opponentName, opponent);
        }
        gameData.updateGame(game);

        connections.broadcast(game.gameID(), null, new LoadGameMessage(chess));
        String moveText = String.format("%s moved %s to %s", username,
                square(move.getStartPosition()), square(move.getEndPosition()));
        if (move.getPromotionPiece() != null) {
            moveText += " and promoted to a " + move.getPromotionPiece().toString().toLowerCase();
        }
        connections.broadcast(game.gameID(), session, new NotificationMessage(moveText));
        if (status != null) {
            connections.broadcast(game.gameID(), null, new NotificationMessage(status));
        }
    }

    private void leave(Session session, String username, GameData game) throws Exception {
        var color = colorOf(username, game);
        if (color != null) {
            // free the seat so someone else can join in this color
            var updated = (color == ChessGame.TeamColor.WHITE)
                    ? new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game())
                    : new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            gameData.updateGame(updated);
        }
        connections.remove(game.gameID(), session);
        connections.broadcast(game.gameID(), session, new NotificationMessage(username + " left the game"));
    }

    private void resign(Session session, String username, GameData game) throws Exception {
        var color = colorOf(username, game);
        if (color == null) {
            throw new GameplayException("observers can't resign");
        }
        if (game.game().isGameOver()) {
            throw new GameplayException("the game is already over");
        }
        game.game().setGameOver(true);
        gameData.updateGame(game);
        connections.broadcast(game.gameID(), null,
                new NotificationMessage(String.format("%s (%s) resigned. The game is over.", username, color)));
    }

    private String authenticate(String authToken) throws Exception {
        if (authToken == null || !authData.validateAuth(authToken)) {
            throw new GameplayException("unauthorized");
        }
        return authData.getUser(authToken).username();
    }

    private GameData loadGame(Integer gameID) throws Exception {
        if (gameID == null || !gameData.validateGameID(gameID)) {
            throw new GameplayException("game not found");
        }
        var game = gameData.getGame(gameID);
        if (game.game() == null) {
            game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
        }
        return game;
    }

    /** The color this user plays in the game, or null if they're observing. */
    private static ChessGame.TeamColor colorOf(String username, GameData game) {
        if (Objects.equals(username, game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(username, game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    private static String playerName(ChessGame.TeamColor color, GameData game) {
        String name = (color == ChessGame.TeamColor.WHITE) ? game.whiteUsername() : game.blackUsername();
        return name == null ? color.toString().toLowerCase() : name;
    }

    private static String square(ChessPosition pos) {
        return "" + (char) ('a' + pos.getColumn() - 1) + pos.getRow();
    }
}
