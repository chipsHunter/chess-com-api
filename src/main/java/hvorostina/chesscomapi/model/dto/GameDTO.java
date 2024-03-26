package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.sql.Timestamp;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {
    @JsonProperty("url")
    String gameURL;
    @JsonProperty("uuid")
    String uuid;
    @JsonProperty("end_time")
    Long gameTimestamp;
    @JsonProperty("time_class")
    String timeClass;
    @JsonProperty("white")
    PlayerInGameDTO whitePlayer;
    @JsonProperty("black")
    PlayerInGameDTO blackPlayer;
}
