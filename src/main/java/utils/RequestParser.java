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

    public static Map<String, String> parseHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headerDict = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) return headerDict;
            String [] tokens = line.split(": ");
            headerDict.put(tokens[0].toLowerCase(), tokens[1]);
            line = bufferedReader.readLine();
        }
        return headerDict;
    }

    public static Map<String, String> parseParameters(String parameterString) {
        return Arrays.stream(parameterString.split("&"))
            .map(url -> URLDecoder.decode(url, StandardCharsets.UTF_8))
            .map(token -> token.split("="))
            .collect(Collectors.toMap(token -> token[0], token -> token[1]));
    }
}
