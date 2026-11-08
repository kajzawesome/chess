package websocket.messages;

/** Tells a client about something another user did (joined, moved, resigned, ...). */
public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
