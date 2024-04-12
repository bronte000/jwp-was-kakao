package webserver.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface AbstractController {

    void doGet(DataOutputStream dos, String commandLine, Map<String, String> headerDict)
        throws IOException, URISyntaxException;

    void doPost(DataOutputStream dos, Map<String, String> body);

    void doDelete();

    void doPut();
}
