package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.HashMap;
import java.util.Random;

public class GameDataAccess {
    HashMap<Integer, GameData> gameList = new HashMap<Integer, GameData>();
    Random random = new Random();

    public int createGame(String gameName) {
        int chessID = random.nextInt();
        boolean b = validGameID(chessID);
        while (!b) {
            chessID = random.nextInt();
            b = validGameID(chessID);
        }
        gameList.put(chessID, new GameData(chessID,null,null,gameName,new ChessGame()));
        return chessID;
    }

    public GameData getGame(int gameID) throws ResponseException {
        if (gameList.containsKey(gameID)) {
            return gameList.getOrDefault(gameID, null);
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }

    public boolean validGameID(int gameID) {
        return !gameList.containsKey(gameID);
    }

    public void updateGame(GameData game) throws ResponseException {
        if (gameList.containsKey(game.gameID())) {
            gameList.remove(game.gameID());
            gameList.put(game.gameID(), game);
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }

    public String listGames() {
        return gameList.toString();
    }

    public void clearAllGames() {
        if (!gameList.isEmpty()) {
            gameList.clear();
        }
    }

    public int numGames() {
        return gameList.size();
    }
}
