package webserver.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import utils.FileIoUtils;
import webserver.ResponseMaker;
import webserver.view.StaticViewResolver;
import webserver.view.TemplateViewResolver;
import webserver.view.ViewResolver;

public class ResourceController implements AbstractController {

    @Override
    public String[] doGet(String path, Map<String, String> headerDict) throws IOException, URISyntaxException {

        ViewResolver viewResolver = new StaticViewResolver();
        if (isTemplatePath(path)) {
            viewResolver = new TemplateViewResolver();
        }

        String filePath = viewResolver.makeFilePath(path);
        byte [] body = FileIoUtils.loadFileFromClasspath(filePath);
        String contentType = Files.probeContentType(Path.of(filePath));

        String responseHeader = ResponseMaker.response200Header(body.length, contentType);
        String responseBody = ResponseMaker.responseBody(body);
        return new String[]{responseHeader, responseBody};
    }

    @Override
    public String[] doPost(Map<String, String> body) {
        throw new UnsupportedOperationException();
    }

    private boolean isTemplatePath(String path) {
        return path.equals("/") || path.endsWith(".html") || path.endsWith(".ico");
    }
}
