package service;

import chess.ChessGame;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.GameData;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class GameService {
    GameDataAccess gameData = new GameDataAccess();
    AuthDataAccess authData = new AuthDataAccess();
    public String listGames(String auth) throws DataAccessException {
        if (authData.validateAuth(auth)) {
            return gameData.listGames();
        }
        else {
            throw new DataAccessException("invalid auth");
        }
    }

    public  int createGame(String auth, String gameName) throws DataAccessException {
        if (authData.validateAuth(auth)) {
            return gameData.createGame(auth, gameName);
        }
        else {
            throw new DataAccessException("not valid auth");
        }
    }

    public String joinGame(String auth, int gameID, String teamColor) {
        if (authData.validateAuth(auth)) {
            var game = gameData.getGame(gameID);
                //update the team desired to join with the new player if null else throw new
                if (Objects.equals(teamColor, "WHITE")) {
                    var updatedGame = new GameData(gameID, authData.getUser(auth).username(), game.blackUsername(), game.gameName(),game.game());
                    gameData.updateGame(updatedGame);
                }
                else if (Objects.equals(teamColor, "BLACK")) {
                    var updatedGame = new GameData(gameID, game.whiteUsername(),authData.getUser(auth).username(), game.gameName(),game.game());
                    gameData.updateGame(updatedGame);
                }
                else {
                    throw new IllegalArgumentException("Already have player for that team");
                }
        }
        return "";
    }

    public String deleteGames() {
        gameData.clearAllGames();
        return "";
    }
}
