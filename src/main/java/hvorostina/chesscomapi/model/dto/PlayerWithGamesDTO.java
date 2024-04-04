package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;

import java.net.URL;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlayerWithGamesDTO {
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
    @JsonProperty("game_list")
    List<GameDTOWithDate> games;
}
