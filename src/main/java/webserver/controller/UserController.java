package webserver.controller;

import db.DataBase;
import java.io.DataOutputStream;
import java.util.Map;
import model.User;
import utils.RequestParser;
import webserver.ResponseMaker;

public class UserController implements AbstractController {

    @Override
    public void doGet(DataOutputStream dos, String commandLine, Map<String, String> headerDict){
            String command = commandLine.split("/user")[1];
            executeCommand(command);
            ResponseMaker.response302Header(dos, "/index.html");
    }

    @Override
    public void doPost(DataOutputStream dos, Map<String, String> body) {
        createUser(body);
        ResponseMaker.response302Header(dos, "/index.html");
    }

    @Override
    public void doDelete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPut() {
        throw new UnsupportedOperationException();
    }

    private void createUser(Map<String, String> parameters) {
        DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"),
            parameters.get("name"), parameters.get("email")));
    }

    private void executeCommand(String command) {
        String[] tokens = command.split("\\?");
        createUser(RequestParser.parseParameters(tokens[1]));
    }
}
