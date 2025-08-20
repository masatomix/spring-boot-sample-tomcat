package nu.mine.kino.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/error-sim")
public class ErrorController {

    @GetMapping("/400")
    public ResponseEntity<String> badRequest() {
        return ResponseEntity.badRequest().body("これは400 Bad Requestです");
    }

    @GetMapping("/500")
    public ResponseEntity<String> internalError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("これは500 INTERNAL_SERVER_ERRORです");
    }

    @GetMapping("/502")
    public ResponseEntity<String> BAD_GATEWAY() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("これは502 BAD_GATEWAYです");
    }

    @GetMapping("/503")
    public ResponseEntity<String> SERVICE_UNAVAILABLE() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("これは503 SERVICE_UNAVAILABLEです");
    }

    @GetMapping("/504")
    public ResponseEntity<String> GATEWAY_TIMEOUT() {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body("これは504 GATEWAY_TIMEOUTです");
    }

    @GetMapping("/throw-exception")
    public String throwException() {
        throw new RuntimeException("わざと例外をスローしました");
    }
}
