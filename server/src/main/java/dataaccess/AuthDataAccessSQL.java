package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class AuthDataAccessSQL {

    public AuthDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public AuthData login(UserData user) throws ResponseException {
        var login = "INSERT INTO auths (AuthToken, User, AuthData) VALUES (?, ?, ?)";
        String userNewAuth = UUID.randomUUID().toString();
        var auth = new AuthData(userNewAuth, user.username());
        executeUpdate(login, userNewAuth, user.username(), new Gson().toJson(auth));
        return auth;
    }

    public void logout(String authToken) throws ResponseException {
        if (validateAuth(authToken)) {
            String logout = "DELETE FROM auths WHERE authToken = ?";
            executeUpdate(logout, authToken);
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }

    public AuthData getUser(String auth) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE AuthToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs); // Assumes readAuth(rs) correctly maps the ResultSet to an AuthData object
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean validateAuth(String auth) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT AuthToken FROM auths WHERE AuthToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
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


    public void clearAllAuths() throws SQLException, ResponseException {
        String clear = "TRUNCATE TABLE auths";
        executeUpdate(clear);
    }

    public int loggedInUsers() {
        String countQuery = "SELECT count(*) FROM auths";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(countQuery)) {
                var rs = ps.executeQuery(); // Execute query and get the result set
                if (rs.next()) {
                    return rs.getInt(1); // Retrieve the count from the first column
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var auth = rs.getString("AuthToken");
        var user = rs.getString("User");
        return new AuthData(auth, user);
    }

    private String executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case AuthData p -> ps.setString(i + 1, p.toString());
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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              AuthToken TEXT NOT NULL,
              User TEXT NOT NULL,
              AuthData TEXT DEFAULT NULL
            );
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
