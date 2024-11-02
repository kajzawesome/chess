package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class UserDataAccessSQL {

    public UserDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM Users WHERE Username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
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

    public void addNewUser(UserData user) throws ResponseException {
        var registerUser = "INSERT INTO Users (Username, Password, Email, User) VALUES (?, ?, ?, ?)";
        UserData databaseUser = new UserData(user.username(), BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email());
        executeUpdate(registerUser, user.username(), databaseUser.password(), user.username(), new Gson().toJson(databaseUser));
    }

    public boolean alreadyRegistered(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT Username FROM Users WHERE Username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void clearAllUsers() throws ResponseException {
        String clear = "TRUNCATE TABLE Users";
        executeUpdate(clear);
    }

    public int numUsers() {
        String countQuery = "SELECT count(*) FROM Users";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(countQuery)) {
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
                        case UserData p -> ps.setString(i + 1, p.toString());
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

    private UserData readUser(ResultSet rs) throws SQLException {
        var user = rs.getString("User");
        return new Gson().fromJson(user, UserData.class);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  Users (
              Username TEXT NOT NULL,
              Password TEXT NOT NULL,
              Email TEXT,
              User TEXT DEFAULT NULL
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

