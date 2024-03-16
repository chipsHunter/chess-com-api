package hvorostina.chesscomapi.model.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class PlayerDTO {
    int playerID;
    String username;
    URL userAccount;
    String country;
    String status;
}
