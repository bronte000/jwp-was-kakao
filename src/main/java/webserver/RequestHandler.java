package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;

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

        String requestLine = bufferedReader.readLine();
        System.out.println(requestLine);

        String commandLine = RequestParser.parseCommandLine(requestLine);
        Map<String, String> headerDict = RequestParser.parseHeader(bufferedReader);
        String body = RequestParser.parseBody(bufferedReader, headerDict);
        AbstractController controller = RequestParser.parseController(commandLine);

        Request request = new Request(commandLine, headerDict, body);
        String command = controller.parseCommand(commandLine);
        Response response = controller.doMethod(command, request);

        dos.writeBytes(response.getResponseHeader());
        dos.write(response.getResponseBody());
    }
}
