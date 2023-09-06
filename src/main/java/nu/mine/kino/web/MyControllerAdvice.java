package nu.mine.kino.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class MyControllerAdvice extends ResponseEntityExceptionHandler {

    // ResponseStatusException で投げてきたとき、400番台はWarn、500番台はErrorなログを出力する
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleException(ResponseStatusException exception, WebRequest request) {
        if (exception.getStatusCode().is4xxClientError()) {
            log.warn(exception.getMessage(), exception);
        } else if (exception.getStatusCode().is5xxServerError()) {
            log.error(exception.getMessage(), exception);
        }
        // exception.printStackTrace();
        return this.handleExceptionInternal(exception, null, new HttpHeaders(), exception.getStatusCode(), request);
    }
}