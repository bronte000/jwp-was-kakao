package webserver.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import utils.FileIoUtils;
import webserver.Request;
import webserver.Response;
import webserver.ResponseMaker;
import webserver.view.StaticViewResolver;
import webserver.view.TemplateViewResolver;
import webserver.view.ViewResolver;

public class ResourceController implements AbstractController {

    private final Map<String, Function<Request, Response>> methodDict = Map.of(
        "GET", this::doGet
    );

    public Response doMethod(String method, Request request) {
        return methodDict.get(method).apply(request);
    }

    public Response doGet(Request request)  {

        ViewResolver viewResolver = new StaticViewResolver();
        if (isTemplatePath(request.getCommandPath())) {
            viewResolver = new TemplateViewResolver();
        }

        try {
            String filePath = viewResolver.makeFilePath(request.getCommandPath());
            byte [] body = FileIoUtils.loadFileFromClasspath(filePath);
            String contentType = Files.probeContentType(Path.of(filePath));

            String responseHeader = ResponseMaker.response200Header(body.length, contentType);
            String responseBody = ResponseMaker.responseBody(body);
            return new Response(responseHeader, responseBody);
        } catch (IOException | URISyntaxException e) {
            return new Response(ResponseMaker.response404Header(), "");
        }
    }

    private boolean isTemplatePath(String path) {
        return path.equals("/") || path.endsWith(".html") || path.endsWith(".ico");
    }
}
