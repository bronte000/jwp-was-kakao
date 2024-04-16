package webserver.Dataclass;

import webserver.SessionManager;

import java.util.Map;

public class Request {

    private final String commandPath;
    private final Map<String, String> headerDict;
    private final String body;
    private final boolean setCookie;

    public Request(String commandPath, Map<String, String> headerDict, String body) {
        this.commandPath = commandPath;
        this.headerDict = headerDict;
        this.body = body;
        this.setCookie = !hasValidCookie();
    }

    public boolean hasValidCookie() {
        if (!headerDict.containsKey("cookie")) {
            return false;
        }
        HttpCookie cookie = new HttpCookie(headerDict.get("cookie"));
        return SessionManager.findSession(cookie.getAttribute("JSESSIONID")) != null;
    }

    public String getCommandPath() {
        return commandPath;
    }

    public String getQuery() {
        if (commandPath.contains("\\?")) {
            return commandPath.split("\\?")[1];
        }
        return body;
    }

    public boolean isSetCookie() {
        return setCookie;
    }

    public HttpCookie getCookie() {
        if (setCookie) {
            return new HttpCookie();
        }
        return new HttpCookie(headerDict.get("cookie"));
    }
}
