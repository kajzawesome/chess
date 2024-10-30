package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.*;

public class GameDataAccessMemory {
    HashMap<Integer, GameData> gameList = new HashMap<Integer, GameData>();
    List<GameData> games = new ArrayList<>();

    Random random = new Random();

    public int createGame(String gameName) {
        int chessID = random.nextInt(1000);
        boolean b = validGameID(chessID);
        while (!b) {
            chessID = random.nextInt();
            b = validGameID(chessID);
        }
        GameData game = new GameData(chessID,null,null,gameName,new ChessGame());
        gameList.put(chessID, game);
        games.add(game);
        games.sort(new Comparator<GameData>() {
            @Override
            public int compare(GameData o1, GameData o2) {
                return o1.gameName().compareTo(o2.gameName());
            }
        });
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

    public boolean validateGameID(int gameID) {
        return gameList.containsKey(gameID);
    }

    public void updateGame(GameData game) throws ResponseException {
        if (gameList.containsKey(game.gameID())) {
            GameData oldGame = gameList.get(game.gameID());
            games.remove(oldGame);
            gameList.remove(game.gameID());
            gameList.put(game.gameID(), game);
            games.add(game);
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }

    public List<GameData> listGames() {
        return games;
    }

    public void clearAllGames() {
        if (!gameList.isEmpty()) {
            gameList.clear();
            games.clear();
        }
    }

    public int numGames() {
        return gameList.size();
    }
}
