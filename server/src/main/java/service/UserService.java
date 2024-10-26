package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    UserDataAccess userData = new UserDataAccess();
    AuthDataAccess authData = new AuthDataAccess();

    public AuthData registerUser(UserData user) throws DataAccessException {
        if (userData.getUser(user.username()) == null) {
            userData.addNewUser(user);
            return authData.login(user);
        }
        else {
            throw new DataAccessException("bad");
        }
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData currUser = userData.getUser(user.username());
        if (currUser != null) {
            if (Objects.equals(currUser.password(), user.password())) {
                return authData.login(user);
            }
            else {
                throw new IllegalArgumentException("incorrect password"); //fix error message header to appropriate number/format
            }
        }
        else {
            throw new IllegalArgumentException("username misspelled or is not registered user");
        }
    }

    public String logout(String auth) {
        if (authData.alreadyLoggedIn(auth)) {
            authData.logout(auth);
            return ""; //not working idk why
        }
        throw new IllegalArgumentException("User is not logged in");
    }

    public void deleteAuth() {
        authData.clearAllAuths();
    }
    public void deleteUsers() {
        userData.clearAllUsers();
    }

}
