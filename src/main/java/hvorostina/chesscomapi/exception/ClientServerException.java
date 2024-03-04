package hvorostina.chesscomapi.exception;

public class ClientServerException extends Throwable {
    public ClientServerException(int err) {
        super("Something bad happens! Error " + err);
    }
}
