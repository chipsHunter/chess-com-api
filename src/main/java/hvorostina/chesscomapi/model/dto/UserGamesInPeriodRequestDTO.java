package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserGamesInPeriodRequestDTO {
    @JsonProperty("username")
    private String username;
    @JsonProperty("start_data")
    private String startData;
    @JsonProperty("end_data")
    private String endData;
}
