package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileIoUtils;

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

            String fileName = parseCommandPath(bufferedReader.readLine());
            Map<String, String> headerDict = parseHeader(bufferedReader);
            String filePath = makeFilePath(fileName, headerDict);
            System.out.println(fileName + ", " + filePath);
            byte[] body = FileIoUtils.loadFileFromClasspath(filePath);

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length, Files.probeContentType(Path.of(filePath)));
            responseBody(dos, body);
        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage());
        }
    }

    private String makeFilePath(String fileName, Map<String, String> headerDict) {
        if (isStatic(fileName, headerDict)) {
            return "./static" + fileName;
        }
        return "./templates" + fileName;
    }

    private boolean isStatic(String commandPath, Map<String, String> headerDict) {
        return !commandPath.endsWith(".html") && !commandPath.endsWith(".ico");
    }

    private String parseCommandPath(String commandLine) {
        String[] tokens = commandLine.split(" ");
        return tokens[1];
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
