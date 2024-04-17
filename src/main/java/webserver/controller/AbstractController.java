package webserver.controller;

import webserver.Request;
import webserver.Response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Function;

public interface AbstractController {

    Response doMethod(String method, Request request);
}
