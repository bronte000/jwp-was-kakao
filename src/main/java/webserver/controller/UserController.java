package webserver.controller;

import db.DataBase;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import model.User;
import utils.RequestParser;
import webserver.Request;
import webserver.Response;
import webserver.ResponseMaker;

public class UserController implements AbstractController {

    private final Map<String, Function<Request, Response>> methodDict = Map.of(
            "GET", this::doGet,
            "POST", this::doPost
    );

    @Override
    public Response doMethod(String method, Request request){
        return methodDict.get(method).apply(request);
    }

    public Response doGet(Request request) {
            String command = request.getCommandPath().split("/user")[1];
            String[] tokens = command.split("\\?");
            return createUser(tokens[1]);
    }

    public Response doPost(Request request) {
        return createUser(request.getCommandPath());
    }

    private Response createUser(String queryLine) {
        Map<String, String> parameters = RequestParser.parseParameters(queryLine);
        DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"),
                parameters.get("name"), parameters.get("email")));

        String responseHeader = ResponseMaker.response302Header("/index.html");
        return new Response(responseHeader, "");
    }
}
