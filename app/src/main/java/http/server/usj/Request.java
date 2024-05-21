package http.server.usj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Request {

    private static final Logger logger = Logger.getLogger(Request.class.getName());

    private String server;
    private int port;
    private String method;
    private String path;
    private Map<String, String> headers;
    private String body = "";

    public Request(String server, int port) {
        this.server = server;
        this.port = port;
        this.path = "/";
        this.headers = new HashMap<>();
        setupLogger();
    }

    private void setupLogger() {
        try {
            // Define the directory for logs
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectory(logDir);
            }
            // Create a FileHandler with the specified directory
            FileHandler fileHandler = new FileHandler(logDir.resolve("request.log").toString());
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting up logger", e);
        }
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        if (this.method.equals("GET")) {
            this.path = path;
        } else {
            this.path = "/";
        }
    }

    public void addHeader(Map<String, String> h) {
        headers.putAll(h);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String send() {
        StringBuilder request = new StringBuilder();
        request.append(method).append(" ").append(path).append(" HTTP/1.1\r\n");
        request.append("Host: ").append(server).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        if (!body.isEmpty()) {
            request.append("Content-Length: ").append(body.getBytes().length).append("\r\n");
        }

        request.append("\r\n").append(body);

        try (Socket socket = new Socket(server, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            logger.info("Sending request to " + server + ":" + port + " - " + method + " " + path);

            out.println(request.toString());
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            logger.info("Received response from " + server + ":" + port + "\n" + response.toString());

            return response.toString();
        } catch (Exception e) {
            logger.severe("Error sending request to " + server + ":" + port + " - " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getBody() {
        return body;
    }

}
