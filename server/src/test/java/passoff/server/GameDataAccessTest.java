package passoff.server;

import dataaccess.GameDataAccess;
import exception.ResponseException;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataAccessTest {
    GameDataAccess gameData = new GameDataAccess();


    @BeforeEach
    void clear() {
        gameData.clearAllGames();
    }

    @Test
    void makeGame() {
        var gameID = gameData.createGame("Chess");
        assertFalse(gameData.validGameID(gameID));
        assertEquals(1, gameData.numGames());
    }

    @Test
    void makeGames() {
        gameData.createGame("Chess1");
        assertEquals(1, gameData.numGames());
        gameData.createGame("Chess2");
        assertEquals(2, gameData.numGames());
        gameData.createGame("Chess3");
        assertEquals(3, gameData.numGames());
    }

    @Test
    void joinGame() throws ResponseException {
        var gameID = gameData.createGame("Chess");
        assertEquals(1, gameData.numGames());
        var currGame = gameData.getGame(gameID);
        gameData.updateGame(new GameData(currGame.gameID(),"kajzawesome", "Gudny", "Chess", currGame.game()));
        assertEquals(1, gameData.numGames());
        var updatedGame = gameData.getGame(gameID);
        assertNotEquals(currGame, updatedGame);
    }

    @Test
    void clearAll() {
        gameData.createGame("Chess1");
        assertEquals(1, gameData.numGames());
        gameData.createGame("Chess2");
        assertEquals(2, gameData.numGames());
        gameData.createGame("Chess3");
        assertEquals(3, gameData.numGames());
        gameData.clearAllGames();
        assertEquals(0, gameData.numGames());
    }
}
