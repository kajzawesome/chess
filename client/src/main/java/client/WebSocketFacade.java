package client;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

/** The client's WebSocket connection for one game. Incoming messages go to the {@link ServerMessageObserver}. */
public class WebSocketFacade extends Endpoint {

    public interface ServerMessageObserver {
        void notify(ServerMessage message);
    }

    private final Gson gson = new Gson();
    private final Session session;

    public WebSocketFacade(String serverUrl, ServerMessageObserver observer) throws ResponseException {
        try {
            URI uri = new URI(serverUrl.replaceFirst("^http", "ws") + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, uri);
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String text) {
                    observer.notify(parse(text));
                }
            });
        } catch (Exception ex) {
            throw new ResponseException(500, "Could not open game connection: " + ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    private ServerMessage parse(String text) {
        var type = gson.fromJson(text, ServerMessage.class).getServerMessageType();
        return switch (type) {
            case LOAD_GAME -> gson.fromJson(text, LoadGameMessage.class);
            case NOTIFICATION -> gson.fromJson(text, NotificationMessage.class);
            case ERROR -> gson.fromJson(text, ErrorMessage.class);
        };
    }

    public void connect(String auth, int gameID) throws ResponseException {
        send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameID));
    }

    public void makeMove(String auth, int gameID, ChessMove move) throws ResponseException {
        send(new MakeMoveCommand(auth, gameID, move));
    }

    public void resign(String auth, int gameID) throws ResponseException {
        send(new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameID));
    }

    public void leave(String auth, int gameID) throws ResponseException {
        send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID));
        try {
            session.close();
        } catch (Exception ignored) {
            // already closed
        }
    }

    private void send(UserGameCommand command) throws ResponseException {
        try {
            session.getBasicRemote().sendText(gson.toJson(command));
        } catch (Exception ex) {
            throw new ResponseException(500, "Lost connection to the game: " + ex.getMessage());
        }
    }
}
