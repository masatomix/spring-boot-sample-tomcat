package nu.mine.kino.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.interceptor.MDCKey;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Component // 有効にする場合はコメントを外して。
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

        // リクエスト・レスポンスのラップ
        HttpServletRequest wrappedRequest = new ReusableRequestWrapper(request);// ContentCachingRequestWrapper はdoFilterのあとに呼ばれる想定のためつかえなかった(Bodyがとれなかった)
        HttpServletResponse wrappedResponse = new ContentCachingResponseWrapper(response);

        // ログ出力
        Map<String, Object> requestMap = createRequestMap(wrappedRequest);
        log.info("FWログ出力(Request)",
                keyValue("request", requestMap),
                keyValue(MDCKey.APP_TYPE.key(), "FW"), // FWの出しているログだよ、ということを明示するフラグ
                keyValue(MDCKey.LOG_TYPE.key(), "request_response"));// Request/Responseを記録しているログだよ、ということを明示するフラグ

        try {
            // 後続処理へ
            filterChain.doFilter(wrappedRequest, wrappedResponse);

        } finally {

            try {
                Map<String, Object> responseMap = getResponseMap(startTime, wrappedResponse);
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

    private Map<String, Object> createRequestMap(HttpServletRequest request) throws IOException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        // ヘッダ
        Map<String, String> headers = new HashMap<>();
        // Enumeration<String> headerNames = request.getHeaderNames();
        // while (headerNames.hasMoreElements()) {
        // String headerName = headerNames.nextElement();
        // headers.put(headerName, request.getHeader(headerName));
        // }
        Collections.list(request.getHeaderNames()).forEach(name -> headers.put(name, request.getHeader(name)));

        // パラメータ取得
        // Map<String, String[]> paramMap = request.getParameterMap();
        // Map<String, Object> parameters = new HashMap<>();
        // for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
        // String key = entry.getKey();
        // String[] value = entry.getValue();
        // // 配列は1個なら文字列、複数ならそのまま配列で保持
        // parameters.put(key, value.length == 1 ? value[0] : value);
        // }

        // パラメータ
        Map<String, Object> parameters = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> parameters.put(key, value.length == 1 ? value[0] : value));

        // body
        String body = request instanceof ReusableRequestWrapper wrapper ? getRequestBody(wrapper) : "";
        Map<String, Object> requestMap = Map.of(
                MDCKey.METHOD.key(), method,
                MDCKey.URI.key(), requestURI,
                "headers", headers,
                "body", body,
                "parameters", parameters
        //
        );
        return requestMap;
    }

    private String getRequestBody(ReusableRequestWrapper request) {
        byte[] content = request.getCachedBody();
        // body = new String(content, StandardCharsets.UTF_8);
        return new String(content, getCharset(request.getCharacterEncoding()));
    }

    private Map<String, Object> getResponseMap(long startTime, HttpServletResponse response) throws IOException {
        int status = response.getStatus();
        String statusStr = String.valueOf(status);

        // 開始時刻を取り出して処理時間を計算
        Long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        String durationStr = String.valueOf(duration);

        // レスポンスヘッダ
        // Map<String, List<String>> headers = new LinkedHashMap<>();
        // for (String name : response.getHeaderNames()) {
        // headers.put(name, new ArrayList<>(response.getHeaders(name)));
        // }

        String body = response instanceof ContentCachingResponseWrapper wrapper ? getResponseBody(wrapper) : "";

        // --- ヘッダは書き戻した後で取得 ---
        Map<String, List<String>> headers = getResponseHeaders(response);

        Map<String, Object> responseMap = Map.of(
                MDCKey.DURATION.key(), durationStr,
                MDCKey.STATUS.key(), statusStr,
                "headers", headers,
                "body", body
        //
        );
        return responseMap;
    }

    private Map<String, List<String>> getResponseHeaders(HttpServletResponse response) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        for (String name : response.getHeaderNames()) {
            headers.put(name, new ArrayList<>(response.getHeaders(name)));
        }
        return headers;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) throws IOException {
        try {
            byte[] content = response.getContentAsByteArray();
            if (content.length == 0)
                return "";

            // String body = new String(content,
            // getCharset(response.getCharacterEncoding()));
            String body = new String(content, StandardCharsets.UTF_8);

            // JSON の場合だけログ出力
            String contentType = response.getContentType();
            if (contentType != null && contentType.toLowerCase().contains("json")) {
                return body;
            } else {
                return ""; // JSON 以外は空文字
            }
        } finally {
            // レスポンスを書き戻す（必須）
            response.copyBodyToResponse();
        }

    }

    private static java.nio.charset.Charset getCharset(String encoding) {
        if (encoding != null) {
            try {
                return java.nio.charset.Charset.forName(encoding);
            } catch (Exception ignored) {
            }
        }
        return StandardCharsets.UTF_8;
    }

}
