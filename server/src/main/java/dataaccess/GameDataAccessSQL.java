package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class GameDataAccessSQL {

    Random random = new Random();

    public GameDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public int createGame(String gameName) throws ResponseException {
        int chessID = random.nextInt(1000);
        boolean b = validGameID(chessID);
        while (!b) {
            chessID = random.nextInt();
            b = validGameID(chessID);
        }
        ChessGame game = new ChessGame();
        String statement = "INSERT INTO Games (GameID, GameName, White, Black, Game, GameData) VALUES (?, ?, ?, ?, ?, ?)";
        GameData gameInfo = new GameData(chessID, null, null, gameName, game);
        executeUpdate(statement, chessID, gameName, null, null, new Gson().toJson(game), new Gson().toJson(gameInfo));
        return chessID;
    }

    public GameData getGame(int gameID) throws DataAccessException, ResponseException {
        if (validateGameID(gameID)) {
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT * FROM Games WHERE GameID = ?";
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
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
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

    public void updateGame(GameData game) throws ResponseException {
       String statement = "UPDATE Games SET White = ?, Black = ?, Game = ?, GameData = ? WHERE gameID = ?";
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), new Gson().toJson(game.getGame()), game, game.gameID());
    }

    public List<GameData> listGames() {
        List<GameData> games = new ArrayList<>();
        int numGames = numGames();
        for (int i = 0; i < numGames; i++) {
            i = i+1; //placeholder
        }
        return games;
    }

    public void clearAllGames() throws ResponseException {
        String clear = "TRUNCATE TABLE Games";
        executeUpdate(clear);
    }

    public int numGames() {
        String count = "SELECT count(*) FROM Games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(count)) {
                var rs = ps.executeQuery();
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
              GameID int NOT NULL,
              GameName varchar(256) NOT NULL,
              White TEXT,
              Black TEXT,
              Game TEXT NOT NULL,
              GameData TEXT DEFAULT NULL
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
