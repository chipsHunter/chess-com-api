package hvorostina.chesscomapi.exception;

import org.springframework.web.reactive.function.client.WebClient;

public class ExceptionHandler extends Throwable {
    int error;
    public ExceptionHandler(int err) {
        super("Something bad happens! Error " + err);
        error = err;
    }
}
