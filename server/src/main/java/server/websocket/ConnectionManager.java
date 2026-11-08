package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Tracks which WebSocket sessions are in which game, and sends messages to them. */
public class ConnectionManager {
    private final Gson gson = new Gson();
    // gameID -> (session -> username)
    private final Map<Integer, Map<Session, String>> games = new ConcurrentHashMap<>();

    public void add(int gameID, Session session, String username) {
        games.computeIfAbsent(gameID, id -> new ConcurrentHashMap<>()).put(session, username);
    }

    public void remove(int gameID, Session session) {
        var sessions = games.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    /** Drops a closed session from every game it was in. */
    public void removeEverywhere(Session session) {
        games.values().forEach(sessions -> sessions.remove(session));
    }

    public void send(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(gson.toJson(message));
        }
    }

    /** Sends to everyone in the game except {@code exclude} (pass null to send to everyone). */
    public void broadcast(int gameID, Session exclude, ServerMessage message) throws IOException {
        var sessions = games.get(gameID);
        if (sessions == null) {
            return;
        }
        for (var session : sessions.keySet()) {
            if (!session.isOpen()) {
                sessions.remove(session);
            } else if (!session.equals(exclude)) {
                send(session, message);
            }
        }
    }
}
