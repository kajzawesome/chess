package dataaccess;

import exception.ResponseException;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataAccessSQLTest {
    GameDataAccessSQL gameData = new GameDataAccessSQL();

    public GameDataAccessSQLTest() throws ResponseException, DataAccessException {
    }


    @BeforeEach
    public void clear() throws ResponseException {
        gameData.clearAllGames();
    }

    @Test
    public void makeGame() throws ResponseException {
        var gameID = gameData.createGame("Chess");
        assertFalse(gameData.validGameID(gameID));
        assertEquals(1, gameData.numGames());
    }

    @Test
    public void badMakeGame() throws ResponseException, DataAccessException {
        var gameID = gameData.createGame("");
        assertFalse(gameData.validGameID(gameID));
        assertEquals(1, gameData.numGames());
        assertEquals(gameData.getGame(gameID).gameName(),  "");
    }

    @Test
    public void makeGames() throws ResponseException {
        gameData.createGame("Chess1");
        assertEquals(1, gameData.numGames());
        gameData.createGame("Chess2");
        assertEquals(2, gameData.numGames());
        gameData.createGame("Chess3");
        assertEquals(3, gameData.numGames());
    }

    @Test
    public void joinGame() throws ResponseException, DataAccessException {
        var gameID = gameData.createGame("Chess");
        assertEquals(1, gameData.numGames());
        var currGame = gameData.getGame(gameID);
        gameData.updateGame(new GameData(currGame.gameID(),"kajzawesome", "Grundy", "Chess", currGame.game()));
        assertEquals(1, gameData.numGames());
        var updatedGame = gameData.getGame(gameID);
        assertNotEquals(currGame, updatedGame);
    }

    @Test
    public void badJoin() throws ResponseException, DataAccessException {
        var gameID = gameData.createGame("Chess");
        assertEquals(1, gameData.numGames());
        ResponseException exception = assertThrows(ResponseException.class, () -> gameData.getGame(1));
        assertEquals("Error: (description of error)", exception.getMessage());
        assertEquals(500, exception.StatusCode());
        assertEquals(1, gameData.numGames());
    }

    @Test
    public void clearAll() throws ResponseException {
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
