package service;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Random;

public class GameService {

    HashMap<Integer, GameData> gameList = new HashMap<Integer, GameData>();
    public String listGames() {
        return  gameList.toString();
    }

    public  String createGame(String auth, String gameName) {
        UserService u = new UserService();
        Random random = new Random();
        if (u.validAuthToken(auth)) {
            int chessID = 0;
            boolean b = validGameID(chessID);
            while (!b) {
                chessID = random.nextInt();
                b = validGameID(chessID);
            }
            gameList.put(chessID, new GameData(chessID,null,null,gameName,new ChessGame()));
        }
        return "";
    }

    public String joinGame(String auth, GameData game) {
        UserService u = new UserService();
        if (u.validAuthToken(auth)) {
            if (gameList.containsKey(game.gameID())) {
                //update the team desired to join with the new player if null else throw new
                if (game.whiteUsername() == null) {
                    GameData copyGame = gameList.get(game.gameID());
                    int copyGameID = game.gameID();
                    gameList.remove(game.gameID());
                    gameList.put(copyGameID,new GameData(copyGameID, u.getUser(auth), copyGame.blackUsername(), copyGame.gameName(), copyGame.getGame()));
                }
                else if (game.blackUsername() == null) {
                    GameData copyGame = gameList.get(game.gameID());
                    int copyGameID = game.gameID();
                    gameList.remove(game.gameID());
                    gameList.put(copyGameID,new GameData(copyGameID, copyGame.whiteUsername(), u.getUser(auth), copyGame.gameName(), copyGame.getGame()));
                }
                else {
                    throw new IllegalArgumentException("Already have player for that team");
                }
            }
            else {
                throw new IllegalArgumentException("invalid game ID");
            }
        }
        return "";
    }

    public String deleteGames() {
        gameList.clear();
        return "";
    }

    public boolean validGameID(int possibleID) {
        return gameList.containsKey(possibleID);
    }
}
