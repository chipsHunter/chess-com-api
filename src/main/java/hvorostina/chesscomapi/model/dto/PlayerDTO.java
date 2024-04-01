package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;

import java.net.URL;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    @Nullable
    @JsonProperty("player_id")
    Integer id;
    @JsonProperty("username")
    String username;
    @JsonProperty("url")
    URL userAccount;
    @JsonProperty("country")
    String country;
    @JsonProperty("status")
    String status;
}
