package webserver;

import java.util.Map;

public class Request {

    private final String commandPath;
    private final Map<String, String> headerDict;
    private final Map<String, String> bodyDict;

    public Request(String commandPath, Map<String, String> headerDict, Map<String, String> bodyDict) {
        this.commandPath = commandPath;
        this.headerDict = headerDict;
        this.bodyDict = bodyDict;
    }

    public Request(String commandPath, Map<String, String> headerDict) {
        this(commandPath, headerDict, null);
    }

    public String getCommandPath() {
        return commandPath;
    }

    public Map<String, String> getHeaderDict() {
        return headerDict;
    }

    public Map<String, String> getBodyDict() {
        return bodyDict;
    }
}
