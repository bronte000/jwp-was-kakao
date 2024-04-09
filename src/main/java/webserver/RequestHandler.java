package webserver;

import db.DataBase;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;
import utils.IOUtils;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public static final Pattern QUERY_PATTERN = Pattern.compile("^[^?#]+\\?([^#]+)");

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

            String commandLine = bufferedReader.readLine();
            byte[] body = {};
            String contentType = null;
            Map<String, String> headerDict = parseHeader(bufferedReader);
            DataOutputStream dos = new DataOutputStream(out);

            if (isGetResource(commandLine)) {
                String filePath = makeFilePath(extractCommand(commandLine));
                body = FileIoUtils.loadFileFromClasspath(filePath);
                contentType = Files.probeContentType(Path.of(filePath));

                response200Header(dos, body.length, contentType);
                responseBody(dos, body);
            }

            if (isGetQuery(commandLine)) {
                ExecuteCommand(extractCommand(commandLine));
                response302Header(dos, "/index.html");
            }

            if (isPost(commandLine)) {
                int contentLength = Integer.parseInt(headerDict.get("content-length"));
                String queryString = IOUtils.readData(bufferedReader, contentLength);
                createUser(queryString);
                response302Header(dos, "/index.html");
            }
        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage());
        }
    }

    private Map<String, String> parseHeader(BufferedReader bufferedReader) throws IOException{
        Map<String, String> headerDict = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) return headerDict;
            String [] tokens = line.split(": ");
            headerDict.put(tokens[0].toLowerCase(), tokens[1]);
            line = bufferedReader.readLine();
        }
        return headerDict;
    }

    private boolean isPost(String commandLine) {
        return commandLine.startsWith("POST");
    }

    private void ExecuteCommand(String command) {
        String[] tokens = command.split("\\?");
        String path = tokens[0];
        if (path.startsWith("/user")) createUser(tokens[1]);
    }

    private void createUser(String query) {
        Map<String, String> parameters = parseParameters(query);
        DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"),
            parameters.get("name"), parameters.get("email")));
    }

    private Map<String, String> parseParameters(String parameterString) {
        return Arrays.stream(parameterString.split("&"))
            .map(url -> URLDecoder.decode(url, StandardCharsets.UTF_8))
            .map(token -> token.split("="))
            .collect(Collectors.toMap(token -> token[0], token -> token[1]));
    }

    private boolean isGetResource(String commandLine) {
        return isGet(commandLine) && !isGetQuery(commandLine);
    }

    private boolean isGet(String commandLine) {
        return commandLine.startsWith("GET");
    }

    private boolean isGetQuery(String commandLine) {
        return isGet(commandLine) && QUERY_PATTERN.matcher(commandLine).find();
    }

    private String makeFilePath(String fileName) {
        if (isStatic(fileName)) {
            return "./static" + fileName;
        }
        return "./templates" + fileName;
    }

    private boolean isStatic(String commandPath) {
        return !commandPath.endsWith(".html") && !commandPath.endsWith(".ico");
    }

    private String extractCommand(String commandLine) {
        String[] tokens = commandLine.split(" ");
        return tokens[1];
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type:" + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectPath) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location:" + redirectPath + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
