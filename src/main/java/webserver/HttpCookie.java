package webserver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final Map<String, String> cookies = new HashMap<String, String>();

    public static String createCookie() {
        String cookie = UUID.randomUUID().toString();
        cookies.put(cookie, "");
        Session session = new Session(cookie);
        SessionManager.add(session);
        return cookie;
    }

    public static void updateCookie(String cookie, String content) {
        cookies.put(cookie, content);
    }

    public static boolean isLogined(String cookie) {
        if (!cookies.containsKey(cookie)) {
            cookies.put(cookie, "");
            return false;
        }
        return cookies.get(cookie)
                .equals("logined=true");
    }
}
