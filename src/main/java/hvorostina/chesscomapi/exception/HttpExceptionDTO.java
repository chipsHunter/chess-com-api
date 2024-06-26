package hvorostina.chesscomapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
