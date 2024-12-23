import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import exception.ResponseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    //register each end point

    public AuthData createUser(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, null, user, AuthData.class);
    }

    public AuthData login(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, null, user, AuthData.class);
    }

    public void logout(String auth) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, auth, null, null);
    }

    public String listGames(String auth) throws ResponseException {
        var path = "/game";
        record listGamesResponse(GameData[] gameData){}
        var response = this.makeRequest("GET", path, auth, null, listGamesResponse.class);
        return Arrays.toString(response.gameData());
    }

    public int createGame(String auth, String gameName) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, auth, gameName, int.class);
    }

    public void joinGame(String auth, int gameID, String teamColor) throws ResponseException {
        var path = "/game";
        record gameToJoin(int gameID, String teamColor) {}
        this.makeRequest("PUT", path, auth, new gameToJoin(gameID, teamColor), int.class);
    }

    //helper functions

    public ChessGame getGame(String auth, int gameID) throws ResponseException {
        var path = "/game";
        var response = this.makeRequest("GET", path, auth, gameID, GameData.class);
        return response.game();
    }

    private <T> T makeRequest(String method, String path, String auth, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeBody(request, http);
            if (auth != null) {
                http.setRequestProperty("Authorization", auth);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}