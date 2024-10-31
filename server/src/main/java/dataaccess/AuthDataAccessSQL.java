package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class AuthDataAccessSQL {

    public AuthDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public AuthData login(UserData user) throws ResponseException {
        var login = "INSERT INTO auth (AuthToken, User) VALUES (?, ?)";
        var auth = executeUpdate(login, user.username());
        return new AuthData(auth, user.username());
    }

    public void logout(String authToken) throws ResponseException {
        String logout = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(logout);
    }

    public AuthData getUser(String auth) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT AuthToken FROM auths WHERE auth=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean validateAuth(String auth) {
        var statement = String.format("SELECT AuthToken FROM auth WHERE AuthToken = '%s'", auth); //is this better than the above?

        return true;
    }


    public void clearAllAuths() throws SQLException, ResponseException {
        String clear = "TRUNCATE TABLE auths";
        executeUpdate(clear);
    }

    public int loggedInUsers() {
        String count = "SELECT count(*) FROM auths";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(count)) {
                ps.setString(1,count);
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
              AuthToken String NOT NULL,
              User String NOT NULL,
              PRIMARY KEY (AuthToken)
            )
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    //make table bit by bit
                    int x = 0;
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
