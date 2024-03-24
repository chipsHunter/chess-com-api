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
    String username;
    @JsonProperty("start data")
    String startData;
    @JsonProperty("end data")
    String endData;
}
