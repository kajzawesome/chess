package model;

import com.google.gson.Gson;

public record UserData(String username, String password, String email) {

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

}
