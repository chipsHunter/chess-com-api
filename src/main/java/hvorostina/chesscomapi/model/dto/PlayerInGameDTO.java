package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerInGameDTO {
    @JsonProperty("username")
    String username;
    @JsonProperty("rating")
    Integer rating;
    @Nullable
    @JsonProperty("result")
    String gameResult;
}
