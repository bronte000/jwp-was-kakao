package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
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

            handleRequest(dos, bufferedReader);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void handleRequest(DataOutputStream dos, BufferedReader bufferedReader)
        throws IOException, URISyntaxException {

        String[] tokens = bufferedReader.readLine().split(" ");
        String method = tokens[0];
        String commandLine = tokens[1];
        Map<String, String> headerDict = RequestParser.parseHeader(bufferedReader);

        AbstractController controller = getController(commandLine);

        if (isGet(method)) {
            handleGetRequest(dos, controller, commandLine, headerDict);
        }

        if (isPost(method)) {
            handlePostMethod(dos, bufferedReader, headerDict, controller);
        }
    }

    private static void handlePostMethod(DataOutputStream dos, BufferedReader bufferedReader,
        Map<String, String> headerDict, AbstractController controller) throws IOException {
        int contentLength = Integer.parseInt(headerDict.get("content-length"));
        String queryString = IOUtils.readData(bufferedReader, contentLength);
        controller.doPost(dos, RequestParser.parseParameters(queryString));
    }

    private static void handleGetRequest(DataOutputStream dos, AbstractController controller,
        String commandLine, Map<String, String> headerDict) throws IOException, URISyntaxException {
        controller.doGet(dos, commandLine, headerDict);
    }

    private AbstractController getController(String commandLine) {
        if (isUserFunction(commandLine)) {
            return new UserController();
        }
        return new ResourceController();
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
