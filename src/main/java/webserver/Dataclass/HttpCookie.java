package webserver.Dataclass;

import webserver.Dataclass.Session;
import webserver.SessionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final Map<String, String> attributes = new HashMap<>();

    public HttpCookie() {
        Session session = SessionManager.createSession();
        attributes.put("JSESSIONID", session.getId());
    }

    public HttpCookie(final String cookieString) {
        String[] cookies = cookieString.split("; ");
        for (String cookie : cookies) {
            String[] keyValue = cookie.split("=");
            attributes.put(keyValue[0], keyValue[1]);
        }
    }

    public String getAttribute(final String name) {
        return attributes.get(name);
    }

    public String parseToString() {
        return attributes.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
