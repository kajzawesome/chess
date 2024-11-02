package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Server {
    UserService  user;
    GameService game;

    public Server() {
        try {
            user = new UserService();
            game = new GameService();
        }
        catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.post("/user", this::createUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        Spark.exception(DataAccessException.class, this::dataExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String createUser(Request req, Response res) throws ResponseException {
        var g = new Gson();
        var newUser = g.fromJson(
                req.body(), UserData.class);
        var createdUser  = user.registerUser(newUser);
        game.updateAuthData(user.getAuthData());
        return g.toJson(createdUser);
    }

    private String login(Request req, Response res) throws ResponseException, DataAccessException {
        var g = new Gson();
        var userLoggingIn = g.fromJson(req.body(), UserData.class);
        var loggedIn = user.login(userLoggingIn);
        game.updateAuthData(user.getAuthData());
        return g.toJson(loggedIn);
    }

    private Object logout(Request req, Response res) throws ResponseException {
        user.logout(req.headers("authorization"));
        game.updateAuthData(user.getAuthData());
        return "{}";
    }

    private String createGame(Request req, Response res) throws DataAccessException, ResponseException {
        var g = new Gson();
        record gameName(String gameName) {}
        var newGame = g.fromJson(req.body(), gameName.class);
        var gameMade = game.createGame(req.headers("authorization"), newGame.gameName);
        return g.toJson(Map.of("gameID", gameMade));
    }

    private String listGames(Request req, Response res) throws DataAccessException, ResponseException {
        var g = new Gson();
        var allGames = game.listGames(req.headers("authorization"));
        record gameList(String title, List<GameData> games) {}
        return g.toJson(new gameList("games", allGames));
    }

    private Object joinGame(Request req, Response res) throws ResponseException, SQLException, DataAccessException {
        var g  = new Gson();
        record gameJoinInfo(String playerColor, int gameID) {}
        var gameToJoin = g.fromJson(req.body(), gameJoinInfo.class);
        game.joinGame(req.headers("authorization"), gameToJoin.gameID, gameToJoin.playerColor);
        return "{}";
    }

    private Object clear(Request req, Response res) throws ResponseException, SQLException {
        game.deleteGames();
        user.deleteAuth();
        user.deleteUsers();
        game.updateAuthData(null);
        return "";
    }

    private void exceptionHandler(ResponseException exception, Request req, Response res) {
        res.status(exception.StatusCode());
        record ExceptionMessage(String message) {}
        res.body(new Gson().toJson(new ExceptionMessage(exception.getMessage())));
    }

    private void dataExceptionHandler(DataAccessException exception, Request req, Response res) {
        res.status(500);
        record ExceptionMessage(String message) {}
        res.body(new Gson().toJson(new ExceptionMessage(exception.getMessage())));
    }

}