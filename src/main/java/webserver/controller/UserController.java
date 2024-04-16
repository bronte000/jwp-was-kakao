package webserver.controller;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import db.DataBase;
import model.User;
import utils.RequestParser;
import webserver.Dataclass.Request;
import webserver.Dataclass.Response;
import webserver.Dataclass.Session;
import webserver.ResponseMaker;
import webserver.SessionManager;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public class UserController implements AbstractController {

    private static final Handlebars handlebar = new Handlebars(
            new ClassPathTemplateLoader("/templates", ".html")
    );

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
        return new Response(responseHeader);
    }

    private Response login(Request request) {
        Session session = SessionManager.findSession(request.getCookie().getAttribute("JSESSIONID"));
        if (session.isLogined()) {
            return new Response(ResponseMaker.response302Header("/index.html"));
        }

        String query = request.getQuery();
        Map<String, String> parameters = RequestParser.parseParameters(query);

        User user = DataBase.findUserById(parameters.get("userId"));
        if (user != null && user.getPassword().equals(parameters.get("password"))) {
            session.setAttribute("user", user);
            String responseHeader = ResponseMaker.response302Header("/index.html");
            return new Response(responseHeader);
        }
        return new Response(ResponseMaker.response302Header("/user/login_failed.html"));
    }

    private Response list(Request request) {
        Session session = SessionManager.findSession(request.getCookie().getAttribute("JSESSIONID"));
        if (!session.isLogined()) {
            return new Response(ResponseMaker.response302Header("/user/login.html"));
        }

        try {
            Template template = handlebar.compile("user/list");
            String listPage = template.apply(DataBase.findAll());
            String responseHeader = ResponseMaker.response200Header(listPage.length(), "text/html", request.isSetCookie());
            return new Response(responseHeader, listPage.getBytes());
        } catch (IOException e) {
            return new Response(ResponseMaker.response404Header());
        }
    }

    @Override
    public Response doMethod(String method, Request request) {
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
