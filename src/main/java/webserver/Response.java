package webserver;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Response {

    public static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    private final String responseHeader;
    private final byte[] responseBody;

    public Response(String responseHeader, byte[] responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public Response(String responseHeader) {
        this(responseHeader, new byte[0]);
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }
}
