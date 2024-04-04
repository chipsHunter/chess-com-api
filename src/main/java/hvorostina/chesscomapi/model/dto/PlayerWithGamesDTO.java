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
    @JsonProperty("player_username")
    String username;
    @JsonProperty("player_account_url")
    URL userAccount;
    @JsonProperty("player_game_list")
    List<GameDTOWithDate> games;
}
