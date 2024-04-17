package webserver.controller;

import db.DataBase;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import model.User;
import utils.RequestParser;
import webserver.HttpCookie;
import webserver.Request;
import webserver.Response;
import webserver.ResponseMaker;

public class UserController implements AbstractController {

    private final Map<String, Function<Request, Response>> methodDict = Map.of(
            "create", this::createUser,
            "login", this::login,
            "list", this::list
    );

    private Response createUser(Request request) {
        String query = request.getQuery();
        Map<String, String> parameters = RequestParser.parseParameters(query);
        DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"),
                parameters.get("name"), parameters.get("email")));

        String responseHeader = ResponseMaker.response302Header("/index.html");
        return new Response(responseHeader, "");
    }

    private Response login(Request request) {
        if (isLogined(request.getCookie())) {
            return new Response(ResponseMaker.response302Header("/index.html"), "");
        }

        String query = request.getQuery();
        Map<String, String> parameters = RequestParser.parseParameters(query);

        User user = DataBase.findUserById(parameters.get("userId"));
        if (user != null && user.getPassword().equals(parameters.get("password"))) {
            HttpCookie.updateCookie(request.getCookie(), "logined=true");
            String responseHeader = ResponseMaker.response302Header("/index.html");
            return new Response(responseHeader, "");
        }
        return new Response(ResponseMaker.response302Header("/user/login_failed.html"), "");
    }

    private Response list(Request request) {
        if (!isLogined(request.getCookie())) {
            return new Response(ResponseMaker.response302Header("/user/login.html"), "");
        }

        String users = DataBase.findAll().stream()
                .map(User::toString)
                .reduce("", (acc, cur) -> acc + cur + "\n");

        String responseHeader = ResponseMaker.response200Header(users.length(), "text/html", request.isSetCookie());
        return new Response(responseHeader, users);
    }

    private boolean isLogined(String cookie) {
        return HttpCookie.isLogined(cookie);
    }

    @Override
    public Response doMethod(String method, Request request){
        return methodDict.get(method).apply(request);
    }

    @Override
    public String parseCommand(String commandPath) {
        String command = commandPath.split("/user")[1];
        if (command.startsWith("/create")) {
            return "create";
        }
        if (command.startsWith("/login")) {
            return "login";
        }
        if (command.startsWith("/list")) {
            return "list";
        }
        return "";
    }
}
