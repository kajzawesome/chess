package websocket.messages;

import chess.ChessGame;

/** Sends the current game state so the client can redraw the board. */
public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
