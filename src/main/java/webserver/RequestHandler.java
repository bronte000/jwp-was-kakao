package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.IOUtils;
import utils.RequestParser;
import webserver.controller.AbstractController;
import webserver.controller.ResourceController;
import webserver.controller.UserController;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            DataOutputStream dos = new DataOutputStream(out);

            String[] tokens = bufferedReader.readLine().split(" ");
            String method = tokens[0];
            String commandLine = tokens[1];
            Map<String, String> headerDict = RequestParser.parseHeader(bufferedReader);

            AbstractController controller = new ResourceController();
            if (isUserFunction(commandLine)) {
                controller = new UserController();
                commandLine = commandLine.split("/user")[1];
            }

            if (isGet(method)) {
                controller.doGet(dos, commandLine, headerDict);
            }

            if (isPost(method)) {
                int contentLength = Integer.parseInt(headerDict.get("content-length"));
                String queryString = IOUtils.readData(bufferedReader, contentLength);
                controller.doPost(dos, RequestParser.parseParameters(queryString));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private boolean isUserFunction(String commandLine) {
        return commandLine.startsWith("/user") && isFunction(commandLine.split("/user")[1]);
    }

    private boolean isFunction(String queryLine) {
        return queryLine.startsWith("/create");
    }

    private boolean isGet(String method) {
        return method.equals("GET");
    }

    private boolean isPost(String method) {
        return method.equals("POST");
    }
}
