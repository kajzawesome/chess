package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ServerFacade(int port) {
        this("http://localhost:" + port);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        return makeRequest("POST", "/user", null, new UserData(username, password, email), AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        record LoginRequest(String username, String password) {}
        return makeRequest("POST", "/session", null, new LoginRequest(username, password), AuthData.class);
    }

    public void logout(String auth) throws ResponseException {
        makeRequest("DELETE", "/session", auth, null, null);
    }

    public int createGame(String auth, String gameName) throws ResponseException {
        record CreateGameRequest(String gameName) {}
        record CreateGameResponse(int gameID) {}
        return makeRequest("POST", "/game", auth, new CreateGameRequest(gameName), CreateGameResponse.class).gameID();
    }

    public List<GameData> listGames(String auth) throws ResponseException {
        record ListGamesResponse(List<GameData> games) {}
        var response = makeRequest("GET", "/game", auth, null, ListGamesResponse.class);
        return (response == null || response.games() == null) ? List.of() : response.games();
    }

    public void joinGame(String auth, int gameID, String playerColor) throws ResponseException {
        record JoinGameRequest(String playerColor, int gameID) {}
        makeRequest("PUT", "/game", auth, new JoinGameRequest(playerColor, gameID), null);
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }

    private <T> T makeRequest(String method, String path, String auth, Object request, Class<T> responseClass)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            // Headers must be set before the body is written, or Java refuses to add them.
            if (auth != null) {
                http.setRequestProperty("Authorization", auth);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, "Could not reach server: " + ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (status / 100 != 2) {
            String message = "Error: request failed (" + status + ")";
            try (InputStream err = http.getErrorStream()) {
                if (err != null) {
                    record ErrorBody(String message) {}
                    var body = new Gson().fromJson(new InputStreamReader(err), ErrorBody.class);
                    if (body != null && body.message() != null) {
                        message = body.message();
                    }
                }
            } catch (Exception ignored) {
                // fall back to the generic message
            }
            throw new ResponseException(status, message);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (responseClass == null) {
            return null;
        }
        try (InputStream respBody = http.getInputStream()) {
            return new Gson().fromJson(new InputStreamReader(respBody), responseClass);
        }
    }
}
