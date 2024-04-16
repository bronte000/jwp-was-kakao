package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseMaker {

    public static final String CHARSET = String.valueOf(StandardCharsets.ISO_8859_1);
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public static String response200Header(int lengthOfBodyContent, String contentType) {
        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type:" + contentType + "\r\n"
                + "Content-Length: " + lengthOfBodyContent + "\r\n"
                + "\r\n";
    }

    public static String response302Header(String redirectPath) {
        return "HTTP/1.1 302 Found \r\n"
                + "Location:" + redirectPath + "\r\n"
                + "\r\n";
    }

    public static String responseBody(byte[] body) throws IOException {
        return new String(body, CHARSET);
    }
}
