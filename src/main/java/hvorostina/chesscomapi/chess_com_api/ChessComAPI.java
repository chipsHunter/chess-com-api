package hvorostina.chesscomapi.chess_com_api;

import com.fasterxml.jackson.databind.JsonNode;
import hvorostina.chesscomapi.exception.ClientServerException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ChessComAPI {
    private static final String BASE_URL = "https://api.chess.com/pub/";
    private final WebClient webClient;

    public ChessComAPI() {
        this.webClient = WebClient
                .builder()
                .baseUrl(BASE_URL)
                .build();
    }

    //WebClient
    public JsonNode getUserByUsername(String username) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("player/" + username).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, error -> Mono.error(new ClientServerException(error.statusCode().value())))
                .bodyToMono(JsonNode.class)
                .onErrorResume(ClientServerException.class, ex -> Mono.empty())
                .block();
    }
}