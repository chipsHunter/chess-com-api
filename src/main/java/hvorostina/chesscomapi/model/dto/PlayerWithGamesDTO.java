package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlayerWithGamesDTO {
    @JsonProperty("player_username")
    private String username;
    @JsonProperty("player_account_url")
    private URL userAccount;
    @JsonProperty("player_game_list")
    private List<GameDTOWithDate> games;
}
