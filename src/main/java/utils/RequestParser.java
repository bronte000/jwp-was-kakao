package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParser {

    static final int HEADER_TYPE = 0;
    static final int HEADER_VALUES = 1;
    static final int COMMANDLINE_METHOD = 0;
    public static final int COMMANDLINE_PATH = 1;

    public static Map<String, String> parseHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headerDict = new HashMap<>();
        String line = bufferedReader.readLine();
        while (isNotEmpty(line)) {
            String [] tokens = line.split(":");
            headerDict.put(tokens[HEADER_TYPE].toLowerCase().trim(), tokens[HEADER_VALUES].trim());
            line = bufferedReader.readLine();
        }
        return headerDict;
    }
    
    private static boolean isNotEmpty(String line)
    {
        return !"".equals(line) && line != null;
    }

    public static Map<String, String> parseParameters(String parameterString) {
        return Arrays.stream(parameterString.split("&"))
            .map(url -> URLDecoder.decode(url, StandardCharsets.UTF_8))
            .map(token -> token.split("="))
            .collect(Collectors.toMap(token -> token[0], token -> token[1]));
    }

    public static String parseMethod(String commandLine) {
        return commandLine.split(" ")[COMMANDLINE_METHOD];
    }

    public static String parseCommandPath(String commandLine) {
        return commandLine.split(" ")[COMMANDLINE_PATH];
    }
}
