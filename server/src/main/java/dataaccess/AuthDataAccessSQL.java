package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class AuthDataAccessSQL {

    public AuthDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public AuthData login(UserData user) {
        return null;
    }

    public void logout(String authToken) {

    }

    public UserData getUser(String auth) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT AuthToken, json FROM auths WHERE auth=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) { //idk if this applies to strings
                        return new UserData(null, null, null); //idk exactly what im doing here
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public boolean validateAuth(String auth) {
        return true;
    }


    public void clearAllAuths() throws SQLException {
        String clear = "TRUNCATE TABLE auths";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(clear)) {
                int x = 0;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int loggedInUsers() {
        //"""
        //SELECT count(*) FROM auths
        //"""
        return 0;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              AuthToken String NOT NULL,
              User UserData NOT NULL,
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
