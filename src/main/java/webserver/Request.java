package webserver;

import java.util.Map;

public class Request {

    private final String commandPath;
    private final boolean setCookie;
    private final Map<String, String> headerDict;
    private final String body;

    public Request(String commandPath, Map<String, String> headerDict, String body) {
        this.commandPath = commandPath;
        this.setCookie = !headerDict.containsKey("cookie");
        this.headerDict = headerDict;
        this.body = body;
    }

    public String getCommandPath() {
        return commandPath;
    }

    public boolean isSetCookie() {
        return setCookie;
    }

    public String getQuery() {
        if (commandPath.contains("\\?")) {
            return commandPath.split("\\?")[1];
        }
        return body;
    }

    public String getCookie() {
        return headerDict.get("cookie").replace("JSESSIONID=", "");
    }

    public String getSessionId() {
        return getCookie();
    }
}
