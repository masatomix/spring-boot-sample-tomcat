package nu.mine.kino.interceptor;

public enum MDCKey {
    REQUEST_ID("requestId"),
    METHOD("method"),
    URI("uri"),
    DURATION("duration"),
    STATUS("status"),
    APP_TYPE("appType"),
    LOG_TYPE("logType");

    private final String key;

    MDCKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}