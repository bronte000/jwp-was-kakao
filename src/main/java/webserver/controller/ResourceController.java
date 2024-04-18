package webserver.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import utils.FileIoUtils;
import webserver.*;
import webserver.view.StaticViewResolver;
import webserver.view.TemplateViewResolver;
import webserver.view.ViewResolver;

public class ResourceController implements AbstractController {

    private final Map<String, Function<Request, Response>> methodDict = Map.of(
        "", this::doGet
    );

    public Response doMethod(String method, Request request) {
        return methodDict.get(method).apply(request);
    }

    public Response doGet(Request request)  {

        ViewResolver viewResolver = new StaticViewResolver();
        if (isTemplatePath(request.getCommandPath())) {
            viewResolver = new TemplateViewResolver();
        }

        Session session = SessionManager.findSession(request.getSessionId());
        try {
            String filePath = viewResolver.makeFilePath(request.getCommandPath());
            if (filePath.endsWith("login.html") && session.isLogined()) {
                return new Response(ResponseMaker.response302Header("/index.html"));
            }
            byte [] responseBody = FileIoUtils.loadFileFromClasspath(filePath);
            String contentType = Files.probeContentType(Path.of(filePath));
            String responseHeader = ResponseMaker.response200Header(responseBody.length, contentType, request.isSetCookie());
            return new Response(responseHeader, responseBody);
        } catch (IOException | URISyntaxException e) {
            return new Response(ResponseMaker.response404Header());
        }
    }

    private boolean isTemplatePath(String path) {
        return path.equals("/") || path.endsWith(".html") || path.endsWith(".ico");
    }

    @Override
    public String parseCommand(String commandPath) {
        return "";
    }
}
