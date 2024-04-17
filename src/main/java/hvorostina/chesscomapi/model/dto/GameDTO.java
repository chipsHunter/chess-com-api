package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {
    @JsonProperty("url")
    private String gameURL;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("end_time")
    private Long gameTimestamp;
    @JsonProperty("time_class")
    private String timeClass;
    @JsonProperty("white")
    private PlayerInGameDTO whitePlayer;
    @JsonProperty("black")
    private PlayerInGameDTO blackPlayer;
}
