package webserver;

public enum RestMethod {
    POST("post"),
    GET("get"),
    DELETE("delete"),
    PUT("put");

    private final String value;

    RestMethod(String value) {
        this.value = value;
    }

    public boolean hasSameValue(String other) {
        return this.value.equals(other.toLowerCase());
    }
}
