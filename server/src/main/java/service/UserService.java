package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    HashMap<String, UserData> validAuth  = new HashMap<String, UserData>();
    HashMap<String, String> users  = new HashMap<String, String>();
    public AuthData registerUser(UserData user) {
        //UserData registeredUser = new UserData(user.username(), user.password(), user.email());
        String userNewAuth = UUID.randomUUID().toString();
        validAuth.put(userNewAuth,user);
        users.put(user.username(),user.password());
        return new AuthData(userNewAuth, user.username());
    }

    public AuthData login(UserData user) {
        if (users.containsKey(user.username())) {
            if (Objects.equals(users.get(user.username()), user.password())) {
                String userNewAuth = UUID.randomUUID().toString();
                validAuth.put(userNewAuth, user);
                return new AuthData(userNewAuth, user.username());
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
        if (validAuthToken(auth)) {
            validAuth.remove(auth);
            return ""; //not working idk why
        }
        throw new IllegalArgumentException("User is not logged in");
    }

    public void deleteAuth() {
        validAuth.clear();
    }
    public void deleteUsers() {
        users.clear();
    }

    public UserData getUser(String auth) {
        return validAuth.get(auth);
    }

    public boolean validAuthToken(String  auth) {
        return auth != null && validAuth.containsKey(auth);
    }

    public int numValidUsers() {
        return validAuth.size();
    }

    public int numRegisteredUsers() {
        return users.size();
    }
}
