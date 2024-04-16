package webserver.controller;

import db.DataBase;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import model.User;
import utils.RequestParser;
import webserver.ResponseMaker;

public class UserController implements AbstractController {

    @Override
    public String[] doGet(String commandLine, Map<String, String> headerDict) {
            String command = commandLine.split("/user")[1];
             String responseHeader = executeCommand(command);
            return new String[]{responseHeader, ""};
    }

    @Override
    public String[] doPost(Map<String, String> body) {
        createUser(body);
        String responseHeader = ResponseMaker.response302Header("/index.html");
        return new String[]{responseHeader, ""};
    }

    private void createUser(Map<String, String> parameters) {
        DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"),
            parameters.get("name"), parameters.get("email")));
    }

    private String executeCommand(String command) {
        String[] tokens = command.split("\\?");
        createUser(RequestParser.parseParameters(tokens[1]));
        return ResponseMaker.response302Header("/index.html");
    }
}
