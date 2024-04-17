package webserver;

public class ResponseMaker {

    public static String response200Header(int lengthOfBodyContent, String contentType, boolean isSetCookie) {
        String cookieLine = makeCookieLine(isSetCookie);
        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type:" + contentType + "\r\n"
                + "Content-Length: " + lengthOfBodyContent + "\r\n"
                + cookieLine
                + "\r\n";
    }

    private static String makeCookieLine(boolean isSetCookie) {
        if (!isSetCookie) {
            return "";
        }
        return "Set-Cookie: " + "JSESSIONID=" + HttpCookie.createCookie() + "\r\n";
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
