package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.sql.SQLException;
import java.util.List;

public class GameDataAccessSQL {

    public GameDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public int createGame(String gameName) {
        return 1;
    }

    public GameData getGame(int gameID) {
        return null;
    }

    public boolean validGameID(int gameID) {
        return true;
    }

    public boolean validateGameID(int gameID) {
        return true;
    }

    public void updateGame(GameData game) {

    }

    public List<GameData> listGames() {
        return null;
    }

    public void clearAllGames() {

    }

    public int numGames() {
        return 0;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  Games (
              'GameID' int NOT NULL,
              'GameName' varchar(256) NOT NULL,
              'White' String,
              'Black' String,
              'Game' ChessGame NOT NULL,
              'GameData' json TEXT DEFAULT NULL,
              PRIMARY KEY(GameID),
              INDEX(White),
              INDEX(Black)
            )
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
