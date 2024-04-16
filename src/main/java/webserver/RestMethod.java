package webserver;

public enum RestMethod {

    POST("post"),
    GET("get");

    private final String value;

    RestMethod(String value) {
        this.value = value;
    }
}
