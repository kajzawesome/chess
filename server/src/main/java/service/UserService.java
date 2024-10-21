package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    public AuthData registerUser(UserData user) {
        //UserData registeredUser = new UserData(user.username(), user.password(), user.email());
        return new AuthData(UUID.randomUUID().toString(), user.username());
    }

    public AuthData login(UserData user) {
        if (Objects.equals(user.getUsername(), user.username()) && Objects.equals(user.getPassword(), user.password())) {
            return new AuthData(UUID.randomUUID().toString(), user.username());
        }
        //else:
        //String error = "{500: not found in database}";
            //throw new DataAccessException(error);
        return null;
    }

    public void logout(AuthData auth) {

    }
}
