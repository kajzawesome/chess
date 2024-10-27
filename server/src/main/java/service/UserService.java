package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import exception.ResponseException;

import java.util.Objects;

public class UserService {
    UserDataAccess userData = new UserDataAccess();
    AuthDataAccess authData = new AuthDataAccess();

    public AuthData registerUser(UserData user) throws ResponseException {
        if (!userData.alreadyRegistered(user)) {
            userData.addNewUser(user);
            return authData.login(user);
        }
        else {
            throw new ResponseException(403, "Error: already taken");
        }
    }

    public AuthData login(UserData user) throws ResponseException {
        UserData currUser = userData.getUser(user.username());
        if (currUser != null) {
            if (Objects.equals(currUser.password(), user.password())) {
                return authData.login(user);
            }
            else {
                throw new ResponseException(401, "Error: unauthorized");
            }
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }

    public String logout(String auth) throws ResponseException {
        if (authData.validateAuth(auth)) {
            authData.logout(auth);
            return "";
        }
        throw new ResponseException(401, "Error: unauthorized");
    }

    public void deleteAuth() throws ResponseException {
        authData.clearAllAuths();
    }
    public void deleteUsers() throws ResponseException {
        userData.clearAllUsers();
    }

    public int numRegistered() {
        return userData.numUsers();
    }

    public UserData getUser(String username) throws ResponseException {
        return userData.getUser(username);
    }
}
