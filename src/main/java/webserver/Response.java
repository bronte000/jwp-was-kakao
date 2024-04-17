package webserver;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Response {

    public static final Charset charset = StandardCharsets.ISO_8859_1;

    private final String responseHeader;
    private final String responseBody;

    public Response(String responseHeader, String responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public String getResponseHeader() {
        return responseHeader;
    }

    public byte[] getResponseBody() {
        return responseBody.getBytes(charset);
    }
}
