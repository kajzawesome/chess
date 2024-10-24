package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    HashMap<String, String> validAuth  = new HashMap<String, String>();
    HashMap<String, String> users  = new HashMap<String, String>();
    public AuthData registerUser(UserData user) {
        //UserData registeredUser = new UserData(user.username(), user.password(), user.email());
        String userNewAuth = UUID.randomUUID().toString();
        validAuth.put(userNewAuth,user.username());
        users.put(user.username(),user.password());
        return new AuthData(userNewAuth, user.username());
    }

    public AuthData login(UserData user) {
        if (users.containsKey(user.username())) {
            if (Objects.equals(users.get(user.username()), user.password())) {
                String userNewAuth = UUID.randomUUID().toString();
                validAuth.put(userNewAuth, user.username());
                return new AuthData(userNewAuth, user.username());
            }
            else {
                throw new IllegalArgumentException("incorrect password");
            }
        }
        else {
            throw new IllegalArgumentException("username misspelled or is not registered user");
        }
    }

    public String logout(String auth) {
        if (validAuthToken(auth)) {
            validAuth.remove(auth);
            return "";
        }
        throw new IllegalArgumentException("User is not logged in");
    }

    public String delete(){
        validAuth.clear();
        return "";
    }

    public String getUser(String auth) {
        return validAuth.get(auth);
    }

    public boolean validAuthToken(String  auth) {
        return auth != null && validAuth.containsKey(auth);
    }
}
