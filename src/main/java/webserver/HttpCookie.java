package webserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final Map<String, String> cookies = new HashMap<String, String>();

    public static String createCookie() {
        String cookie = UUID.randomUUID().toString();
        cookies.put(cookie, "");
        return cookie;
    }

    public static void updateCookie(String cookie, String content) {
        cookies.replace(cookie, content);
    }

    public static boolean isLogined(String cookie) {
        return cookies.get(cookie).equals("logined=true");
    }
}
