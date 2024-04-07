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
    String exceptionName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("request")
    String request;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("method")
    String exceptionInfo;
    @JsonProperty("time")
    String time;
}
