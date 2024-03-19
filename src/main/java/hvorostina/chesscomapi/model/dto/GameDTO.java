package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {
    @JsonProperty("url")
    URL gameURL;
    @JsonProperty("uuid")
    String uuid;
    @JsonProperty("end_time")
    Timestamp gameTimestamp;
    LocalDateTime gameData;
    @JsonProperty("time_class")
    String timeClass;
    @JsonProperty("white")
    PlayerInGameDTO whitePlayer;
    @JsonProperty("black")
    PlayerInGameDTO blackPlayer;
}
