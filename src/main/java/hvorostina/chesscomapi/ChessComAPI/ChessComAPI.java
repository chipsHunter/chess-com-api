package hvorostina.chesscomapi.ChessComAPI;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

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
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("player/" + username).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
