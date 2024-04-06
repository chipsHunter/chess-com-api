package hvorostina.chesscomapi.exception;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.LocalDateTime;

@ControllerAdvice
public class CustomHttpExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpExceptionDTO exceptionDTO =  HttpExceptionDTO.builder()
                .exceptionName(ex.getMessage())
                .time(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(exceptionDTO, status);
    }
    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<Object> handleAllHttpClientErrorExceptions(HttpClientErrorException ex, WebRequest request) {
        HttpExceptionDTO exceptionDTO =  HttpExceptionDTO.builder()
                .exceptionName(ex.getMessage())
                .exceptionInfo(ex.getStackTrace()[0].getMethodName())
                .time(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(exceptionDTO, ex.getStatusCode());
    }
    @ExceptionHandler(HttpServerErrorException.class)
    protected ResponseEntity<Object> handleAllHttpServerErrorExceptions(HttpServerErrorException ex) {
        HttpExceptionDTO exceptionDTO =  HttpExceptionDTO.builder()
                .exceptionName(ex.getMessage())
                .exceptionInfo(ex.getStackTrace()[0].getMethodName())
                .time(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(exceptionDTO, ex.getStatusCode());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, WebRequest request) {
        HttpExceptionDTO exceptionDTO =  HttpExceptionDTO.builder()
                .exceptionName(ex.getMessage())
                .request(request.getDescription(false))
                .time(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(exceptionDTO, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpExceptionDTO exceptionDTO =  HttpExceptionDTO.builder()
                .exceptionName(ex.getMessage())
                .request(request.getDescription(false))
                .time(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(exceptionDTO, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        HttpExceptionDTO exceptionDTO =  HttpExceptionDTO.builder()
                .exceptionName(ex.getMessage())
                .exceptionInfo(ex.getStackTrace()[0].getMethodName())
                .time(LocalDateTime.now().toString())
                .build();
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
