package webserver.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface AbstractController {

    String[] doGet(String commandLine, Map<String, String> headerDict)
        throws IOException, URISyntaxException;

    String[] doPost(Map<String, String> body) throws IOException;
}
