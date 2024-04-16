package webserver.controller;

import webserver.Dataclass.Request;
import webserver.Dataclass.Response;

public interface AbstractController {

    Response doMethod(String method, Request request);

    String parseCommand(String commandPath);
}
