package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public String getWhitePlayer() {
        return this.whiteUsername;
    }

    public String getBlackPlayer() {
        return this.blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public ChessGame getGame() { return this.game;}

    public String toString() {
        return new Gson().toJson(this);
    }
}
