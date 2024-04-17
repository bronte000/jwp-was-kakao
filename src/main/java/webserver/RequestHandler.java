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

    private final Socket connection;

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
        throws IOException{

        String commandLine = bufferedReader.readLine();
        RestMethod method = RestMethod.valueOf(RequestParser.parseMethod(commandLine).toUpperCase());
        String commandPath = RequestParser.parseCommandPath(commandLine);
        Map<String, String> headerDict = RequestParser.parseHeader(bufferedReader);

        AbstractController controller = getController(commandPath);

        Response response = null;
        if (RestMethod.GET == method) {
            response = handleGetRequest(controller, commandPath, headerDict);
        }

        if (RestMethod.POST == method) {
            response = handlePostMethod(bufferedReader, headerDict, controller);
        }

        dos.writeBytes(response.getResponseHeader());
        dos.write(response.getResponseBody());
    }

    private static Response handlePostMethod(BufferedReader bufferedReader,
        Map<String, String> headerDict, AbstractController controller) throws IOException {
        int contentLength = Integer.parseInt(headerDict.get("content-length"));
        String queryString = IOUtils.readData(bufferedReader, contentLength);

        Request request = new Request(queryString, headerDict);
        return controller.doMethod("POST", request);
    }

    private static Response handleGetRequest(AbstractController controller,
        String commandLine, Map<String, String> headerDict) {

        Request request = new Request(commandLine, headerDict);
        return controller.doMethod("GET", request);
    }

    private AbstractController getController(String commandLine) {
        if (isUserFunction(commandLine)) {
            return new UserController();
        }
        return new ResourceController();
    }

    private boolean isUserFunction(String commandPath) {
        return commandPath.startsWith("/user") && isFunction(commandPath.split("/user")[1]);
    }

    private boolean isFunction(String queryLine) {
        return queryLine.startsWith("/create");
    }
}
