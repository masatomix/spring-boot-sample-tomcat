package nu.mine.kino.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.exceptions.ClientException;
import nu.mine.kino.exceptions.ServerException;

/**
 * アプリたち独自の例外を、HTTP Status付きのResposeへ載せ替えるHandler
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException exception) {
        var body = new ErrorResponse("BAD_REQUEST", exception.getMessage());
        log.warn(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ServerException.class)
    // @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException exception) {
        var body = new ErrorResponse("SERVICE_UNAVAILABLE", exception.getMessage());
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    // 汎用
    // @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    // public ErrorResponse handleGeneric(Exception ex) {
    // return new ErrorResponse("INTERNAL_ERROR", "予期しないエラーが発生しました");
    // }

    // Lombok の @Value (Immutableな@Data)みたいなもん
    record ErrorResponse(String code, String message) {
    }
}