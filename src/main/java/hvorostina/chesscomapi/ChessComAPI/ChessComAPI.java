package hvorostina.chesscomapi.ChessComAPI;

import com.fasterxml.jackson.databind.JsonNode;
import hvorostina.chesscomapi.exception.ExceptionHandler;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Component
public class ChessComAPI {
    private final String BASE_URL = "https://api.chess.com/pub/";
    private final WebClient webClient;

    public ChessComAPI() {
        this.webClient = WebClient
                .builder()
                .baseUrl(BASE_URL)
                .build();
    }

    //WebClient
    public JsonNode getUserByUsername(String username) {
        JsonNode response =  webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("player/" + username).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, error -> Mono.error(new ExceptionHandler(error.statusCode().value())))
                .bodyToMono(JsonNode.class)
                .onErrorResume(ExceptionHandler.class, ex -> Mono.empty())
                .block();
        return response;
    }
}