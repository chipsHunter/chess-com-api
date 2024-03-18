package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {
    @JsonProperty("url")
    String gameURL;
    @JsonProperty("uuid")
    String UUID;
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
