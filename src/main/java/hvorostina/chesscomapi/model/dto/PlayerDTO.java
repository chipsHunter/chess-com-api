package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    @JsonProperty("player_id")
    int playerID;
    @JsonProperty("username")
    String username;
    @JsonProperty("url")
    URL userAccount;
    @JsonProperty("country")
    String country;
    @JsonProperty("status")
    String status;
}
