package nu.mine.kino.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;



import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Component
@Slf4j
public class MDCLoggingInterceptor implements HandlerInterceptor {

    // 1リクエスト内(=スレッド内)で共通に使いたい値をココに定義する。
    private static final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>(); // リクエスト開始時間
    private static final ThreadLocal<String> requestIdThreadLocal = new ThreadLocal<>(); // リクエストID

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        String requestId = UUID.randomUUID().toString(); // リクエストIDを生成
        long startTime = System.currentTimeMillis(); // リクエスト開始時間を記録
        requestIdThreadLocal.set(requestId);
        startTimeThreadLocal.set(startTime);

        // 全てのリクエストに出力したいプロパティはココで設定する。
        MDC.put(MDCKey.REQUEST_ID.key(), requestId);

        // レスポンスヘッダにも付けると呼び元でも追跡できる
        response.setHeader("X-Request-Id", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        try {
            // スレッドから値を取り出す
            String requestId = requestIdThreadLocal.get();
            long startTime = startTimeThreadLocal.get();
            

            String method = request.getMethod();
            String requestURI = request.getRequestURI();
            int status = response.getStatus();
            String statusStr = String.valueOf(status);

            // 開始時刻を取り出して処理時間を計算
            Long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
        
            String durationStr = String.valueOf(duration);

            // FWの出しているログだよ、ということを明示するフラグ
            MDC.put(MDCKey.APP_TYPE.key(), "FW");
            // Request/Responseを記録しているログだよ、ということを明示するフラグ
            MDC.put(MDCKey.LOG_TYPE.key(), "request_response");

            // MDC.put(MDCKey.METHOD.key(), method);
            // MDC.put(MDCKey.URI.key(), requestURI);
            // MDC.put(MDCKey.DURATION.key(), durationStr);
            // MDC.put(MDCKey.STATUS.key(), statusStr);
            // log.info("FWログ出力");

            Map<String, Object> requestMap = Map.of(
                    MDCKey.METHOD.key(), method,
                    MDCKey.URI.key(), requestURI
            //
            );
            Map<String, Object> responseMap = Map.of(
                    MDCKey.DURATION.key(), durationStr,
                    MDCKey.STATUS.key(), statusStr
            //
            );
            log.info("FWログ出力", keyValue("request", requestMap), keyValue("response",
                    responseMap));

        } finally {
            startTimeThreadLocal.remove();
            requestIdThreadLocal.remove();
            for (MDCKey key : MDCKey.values()) {
                MDC.remove(key.key());
            }
        }

    }
}
