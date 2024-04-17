package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ResponseMaker {

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

    public static String response404Header() {
        return "HTTP/1.1 404 Not Found \r\n"
                + "Content-Type: text/html;charset=utf-8\r\n"
                + "\r\n"
                + "<html><body><h1>404 Not Found</h1></body></html>";
    }

    public static String responseBody(byte[] body) {
        return new String(body, Response.charset);
    }

}
