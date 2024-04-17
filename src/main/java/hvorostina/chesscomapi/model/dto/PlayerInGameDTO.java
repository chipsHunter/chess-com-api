package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerInGameDTO {
    @JsonProperty("username")
    private String username;
    @JsonProperty("rating")
    private Integer rating;
    @JsonProperty("result")
    private String gameResult;
}
