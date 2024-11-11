package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import exception.ResponseException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {
    UserDataAccessSQL userData = new UserDataAccessSQL();
    AuthDataAccessSQL authData = new AuthDataAccessSQL();

    public UserService() throws ResponseException, DataAccessException {
    }

    public AuthData registerUser(UserData user) throws ResponseException {
        if (userData.alreadyRegistered(user.username())) {
            throw new ResponseException(403, "Error: already taken");
        }
        if (user.password() == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        var registeredUser = new UserData(user.username(), (BCrypt.hashpw(user.password(), BCrypt.gensalt())),  user.email());
        userData.addNewUser(registeredUser);
        return authData.login(registeredUser);
    }

    public AuthData login(UserData user) throws ResponseException, DataAccessException {
        if (user == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        var currUser = userData.getUser(user.username());
        if (currUser == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        var hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        if (BCrypt.checkpw(user.password(), currUser.password())) {
            return authData.login(user);
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public String logout(String auth) throws ResponseException {
        if (validateAuth(auth)) {
            authData.logout(auth);
            return "{}";
        }
        else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public void deleteAuth() throws ResponseException, SQLException {
        authData.clearAllAuths();
    }
    public void deleteUsers() throws ResponseException {
        userData.clearAllUsers();
    }

    public int numRegistered() {
        return userData.numUsers();
    }

    public UserData getUser(String username) throws ResponseException, DataAccessException {
        return userData.getUser(username);
    }

    public boolean validateAuth(String auth) {
        return authData.validateAuth(auth);
    }

    public int loggedInUsers() {
        return authData.loggedInUsers();
    }

    public AuthDataAccessSQL getAuthData() {
        return authData;
    }
}
