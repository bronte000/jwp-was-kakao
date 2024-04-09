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
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

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

            String command = parseCommandPath(bufferedReader.readLine());
            byte[] body = {};
            String contentType = null;

            if (isQuery(command)) {
                ExecuteCommand(command);
            }
            if (isResource(command)) {
                String filePath = makeFilePath(command);
                body = FileIoUtils.loadFileFromClasspath(filePath);
                contentType = Files.probeContentType(Path.of(filePath));
            }

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage());
        }
    }

    private void ExecuteCommand(String command) {
        String[] tokens = command.split("\\?");
        String path = tokens[0];
        Map<String, String> parameters = Arrays.stream(tokens[1].split("&"))
            .map(url -> URLDecoder.decode(url, StandardCharsets.UTF_8))
            .map(token -> token.split("="))
            .collect(Collectors.toMap(token -> token[0], token -> token[1]));

        if (path.startsWith("/user")) {
            DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"),
                parameters.get("name"), parameters.get("email")));

            System.out.println(DataBase.findUserById("test"));
        }
    }

    private boolean isResource(String command) {
        return !isQuery(command);
    }

    private boolean isQuery(String command) {
        return QUERY_PATTERN.matcher(command).find();
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

    private String parseCommandPath(String commandLine) {
        String[] tokens = commandLine.split(" ");
        return tokens[1];
    }

//    private Map<String, String> parseHeader(BufferedReader bufferedReader) throws IOException{
//        Map<String, String> headerDict = new HashMap<>();
//        String line = bufferedReader.readLine();
//        while (!"".equals(line)) {
//            if (line == null) return headerDict;
//            String [] tokens = line.split(": ");
//            headerDict.put(tokens[0].toLowerCase(), tokens[1]);
//            line = bufferedReader.readLine();
//        }
//        return headerDict;
//    }

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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
