package hvorostina.chesscomapi.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDTO {
    @Nullable
    @JsonProperty("player_id")
    private Integer id;
    @NotBlank
    @JsonProperty("username")
    private String username;
    @JsonProperty("url")
    private URL userAccount;
    @JsonProperty("country")
    private String country;
    @JsonProperty("status")
    private String status;
}
