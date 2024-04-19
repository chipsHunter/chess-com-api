package hvorostina.chesscomapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpExceptionDTO {
    @JsonProperty("exception")
    private String exceptionName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("request")
    private String request;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("method")
    private String exceptionInfo;
    @JsonProperty("time")
    private String time;
}
