package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import exception.ResponseException;
import model.GameData;

import java.util.Objects;

public class GameService {
    GameDataAccess gameData = new GameDataAccess();
    AuthDataAccess authData;

    public String listGames(String auth) throws ResponseException {
        if (authData != null) {
            if (authData.validateAuth(auth)) {
                return gameData.listGames();
            } else {
                throw new ResponseException(401, "Error: unauthorized");
            }
        }
        else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public  int createGame(String auth, String gameName) throws ResponseException {
        if (authData.validateAuth(auth)) {
            return gameData.createGame(gameName);
        }
        else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public String joinGame(String auth, int gameID, String teamColor) throws ResponseException {
        if (authData.validateAuth(auth)) {
            var game = gameData.getGame(gameID);
                //update the team desired to join with the new player if null else throw new
                if (Objects.equals(teamColor, "WHITE")) {
                    if (game.whiteUsername() == null) {
                        var updatedGame = new GameData(gameID, authData.getUser(auth).username(), game.blackUsername(), game.gameName(), game.game());
                        gameData.updateGame(updatedGame);
                    }
                    else {
                        throw new ResponseException(400, "Error: bad request");
                    }
                }
                else if (Objects.equals(teamColor, "BLACK")) {
                    if (game.blackUsername() == null) {
                        var updatedGame = new GameData(gameID, game.whiteUsername(), authData.getUser(auth).username(), game.gameName(), game.game());
                        gameData.updateGame(updatedGame);
                    }
                    else {
                        throw new ResponseException(400, "Error: bad request");
                    }
                }
                else {
                    throw new ResponseException(400, "Error: bad request");
                }
        }
        else {
            throw new ResponseException(401, "Error: unauthorized");
        }
        return "";
    }

    public String deleteGames() {
        gameData.clearAllGames();
        authData = null;
        return "";
    }

    public void updateAuthData(AuthDataAccess auths) {
        authData = auths;
    }

    public int numGames() {
        return gameData.numGames();
    }

    public GameData getGame(int gameID) throws ResponseException {
        return gameData.getGame(gameID);
    }
}
