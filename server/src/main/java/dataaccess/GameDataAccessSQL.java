package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class GameDataAccessSQL {

    public GameDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public int createGame(String gameName) {

        return 1;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT GameID FROM Games WHERE GameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean validGameID(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT GameID FROM Games WHERE GameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    return !rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateGameID(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT GameID FROM Games WHERE GameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGame(GameData game) {
        //"UPDATE Games SET col = ? ... WHERE gameID"
    }

    public List<GameData> listGames() {
        return null;
    }

    public void clearAllGames() throws ResponseException {
        String clear = "TRUNCATE TABLE Games";
        executeUpdate(clear);
    }

    public int numGames() {
        String count = "SELECT count(*) FROM Games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(count, RETURN_GENERATED_KEYS)) {
                ps.setString(1,count);
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private String executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case GameData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getString(1);
                }
                return "";
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var game = rs.getString("GameData");
        return new Gson().fromJson(game, GameData.class);
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
