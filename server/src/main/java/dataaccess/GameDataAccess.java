package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Random;

public class GameDataAccess {
    HashMap<Integer, GameData> gameList = new HashMap<Integer, GameData>();
    AuthDataAccess authTokenAccess = new AuthDataAccess();
    Random random = new Random();

    public int createGame(String auth, String gameName) {
        int chessID = 0;
        boolean b = validGameID(chessID);
        while (!b) {
            chessID = random.nextInt();
            b = validGameID(chessID);
        }
        gameList.put(chessID, new GameData(chessID,authTokenAccess.getUser(auth).username(),null,gameName,new ChessGame()));
        return chessID;
    }

    public GameData getGame(int gameID) {
        return gameList.getOrDefault(gameID, null);
    }

    public boolean validGameID(int gameID) {
        return !gameList.containsKey(gameID);
    }

    public void updateGame(GameData game) {
        gameList.remove(game.gameID());
        gameList.put(game.gameID(),game);
    }

    public String listGames() {
        return gameList.toString();
    }

    public void clearAllGames() { gameList.clear();}
}
