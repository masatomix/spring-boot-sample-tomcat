package nu.mine.kino.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.exceptions.ClientException;
import nu.mine.kino.exceptions.ServerException;
import nu.mine.kino.interceptor.MDCKey;

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
        log.warn(exception.getMessage(), keyValue(MDCKey.APP_TYPE.key(), "FW"), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ServerException.class)
    // @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException exception) {
        var body = new ErrorResponse("SERVICE_UNAVAILABLE", exception.getMessage());
        log.error(exception.getMessage(), keyValue(MDCKey.APP_TYPE.key(), "FW"), exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);

    }

    // -------------------------------
    // 想定外の例外 → 500 INTERNAL_SERVER_ERROR
    // -------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        var body = new ErrorResponse("INTERNAL_SERVER_ERROR", "予期しないエラーが発生しました");
        log.error("Unhandled exception occurred", keyValue(MDCKey.APP_TYPE.key(), "FW"), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // -------------------------------
    // 致命的エラー → ログだけ残す
    // -------------------------------
    @ExceptionHandler(Throwable.class)
    public void logFatalThrowable(Throwable t) throws Throwable {
        log.error("Fatal throwable occurred", keyValue(MDCKey.APP_TYPE.key(), "FW"), t);
        throw t; // 伝播させてアプリケーションを落とす
    }

    // Lombok の @Value (Immutableな@Data)みたいなもん
    record ErrorResponse(String code, String message) {
    }
}