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
    public void doGet(DataOutputStream dos,
                        String path,
                        Map<String, String> headerDict) throws IOException, URISyntaxException {

        ViewResolver viewResolver = new StaticViewResolver();
        if (isTemplatePath(path)) {
            viewResolver = new TemplateViewResolver();
        }

        String filePath = viewResolver.makeFilePath(path);
        byte [] body = FileIoUtils.loadFileFromClasspath(filePath);
        String contentType = Files.probeContentType(Path.of(filePath));

        ResponseMaker.response200Header(dos, body.length, contentType);
        ResponseMaker.responseBody(dos, body);
    }

    @Override
    public void doPost(DataOutputStream dos, Map<String, String> body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doDelete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPut() {
        throw new UnsupportedOperationException();
    }

    private boolean isTemplatePath(String path) {
        return path.endsWith(".html") || path.endsWith(".ico");
    }
}
