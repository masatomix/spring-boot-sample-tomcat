package nu.mine.kino.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.interceptor.MDCKey;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

// @Component // 有効にする場合はコメントを外して。
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString(); // リクエストIDを生成
        long startTime = System.currentTimeMillis(); // リクエスト開始時間を記録

        // 全てのリクエストに出力したいプロパティはココで設定する。
        MDC.put(MDCKey.REQUEST_ID.key(), requestId);

        // レスポンスヘッダにも付けると呼び元でも追跡できる
        response.setHeader("X-Request-Id", requestId);

        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        Map<String, Object> requestMap = Map.of(
                MDCKey.METHOD.key(), method,
                MDCKey.URI.key(), requestURI
        //
        );
        log.info("FWログ出力(Request)",
                keyValue("request", requestMap),
                keyValue(MDCKey.APP_TYPE.key(), "FW"), // FWの出しているログだよ、ということを明示するフラグ
                keyValue(MDCKey.LOG_TYPE.key(), "request_response"));// Request/Responseを記録しているログだよ、ということを明示するフラグ

        try {
            // 後続処理へ
            filterChain.doFilter(request, response);

        } finally {
            try {
                int status = response.getStatus();
                String statusStr = String.valueOf(status);

                // 開始時刻を取り出して処理時間を計算
                Long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                String durationStr = String.valueOf(duration);

                Map<String, Object> responseMap = Map.of(
                        MDCKey.DURATION.key(), durationStr,
                        MDCKey.STATUS.key(), statusStr
                //
                );
                log.info("FWログ出力(Request/Response)",
                        keyValue("request", requestMap),
                        keyValue("response", responseMap),
                        keyValue(MDCKey.APP_TYPE.key(), "FW"), // FWの出しているログだよ、ということを明示するフラグ
                        keyValue(MDCKey.LOG_TYPE.key(), "request_response")// Request/Responseを記録しているログだよ、ということを明示するフラグ
                );
            } finally {
                // 後始末
                for (MDCKey key : MDCKey.values()) {
                    MDC.remove(key.key());
                }
            }
        }
    }
}