package service;

import dataaccess.AuthDataAccessMemory;
import dataaccess.GameDataAccessMemory;
import exception.ResponseException;
import model.GameData;

import java.util.List;
import java.util.Objects;

public class GameService {
    GameDataAccessMemory gameData = new GameDataAccessMemory();
    AuthDataAccessMemory authData;

    public List<GameData> listGames(String auth) throws ResponseException {
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

    public void joinGame(String auth, int gameID, String teamColor) throws ResponseException {
        if (authData.validateAuth(auth)) {
            if (gameData.validateGameID(gameID)) {
                var game = gameData.getGame(gameID);
                if (Objects.equals(teamColor, "WHITE")) {
                    if (game.whiteUsername() == null) {
                        var updatedGame = new GameData(gameID, authData.getUser(auth).username(), game.blackUsername(), game.gameName(), game.game());
                        gameData.updateGame(updatedGame);
                    } else {
                        throw new ResponseException(403, "Error: already taken");
                    }
                } else if (Objects.equals(teamColor, "BLACK")) {
                    if (game.blackUsername() == null) {
                        var updatedGame = new GameData(gameID, game.whiteUsername(), authData.getUser(auth).username(), game.gameName(), game.game());
                        gameData.updateGame(updatedGame);
                    } else {
                        throw new ResponseException(403, "Error: already taken");
                    }
                } else {
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
    }

    public void deleteGames() {
        gameData.clearAllGames();
        authData = null;
    }

    public void updateAuthData(AuthDataAccessMemory auths) {
        authData = auths;
    }

    public int numGames() {
        return gameData.numGames();
    }

    public GameData getGame(int gameID) throws ResponseException {
        return gameData.getGame(gameID);
    }
}
