package http.server.usj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Request {

    // Logger instance for logging request and response details
    private static final Logger logger = Logger.getLogger(Request.class.getName());

    // Server address
    private String server;
    // Server port
    private int port;
    // HTTP method (GET, POST, etc.)
    private String method;
    // Request path
    private String path;
    // Headers map to store HTTP headers
    private Map<String, String> headers = new HashMap<>();
    // Request body
    private String body = "";

    // Constructor to initialize the Request object with server and port
    public Request(String server, int port) {
        this.server = server;
        this.port = port;
        this.path = "/";
        setupLogger(); // Setup logger to log request and response details
    }

    // Setup logger to log request and response details to a file
    private void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("request.log"); // Log file name
            SimpleFormatter formatter = new SimpleFormatter(); // Simple formatter for log messages
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO); // Set log level to INFO
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting up logger", e);
        }
    }

    // Set the HTTP method for the request
    public void setMethod(String method) {
        this.method = method;
    }

    // Set the request path, default to "/" if not GET method
    public void setPath(String path) {
        if (this.method.equals("GET")) {
            this.path = path;
        } else {
            this.path = "/";
        }
    }

    // Add multiple headers to the request
    public void addHeader(Map<String, String> h) {
        headers.putAll(h);
    }

    // Add a single header to the request
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    // Set the body of the request
    public void setBody(String body) {
        this.body = body;
    }

    // Send the request and receive the response
    public String send() {
        // Build the HTTP request string
        StringBuilder request = new StringBuilder();
        request.append(method).append(" ").append(path).append(" HTTP/1.1\r\n");
        request.append("Host: ").append(server).append("\r\n");

        // Add headers to the request
        for (Map.Entry<String, String> header : headers.entrySet()) {
            request.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        // Add Content-Length header if the body is not empty
        if (!body.isEmpty()) {
            request.append("Content-Length: ").append(body.getBytes().length).append("\r\n");
        }

        // End of headers and add the body
        request.append("\r\n").append(body);

        try (Socket socket = new Socket(server, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Log the request details
            logger.info("Sending request to " + server + ":" + port + " - " + method + " " + path);

            // Send the request
            out.println(request.toString());

            // Read and build the response
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            // Log the response details
            logger.info("Received response from " + server + ":" + port + "\n" + response.toString());

            // Return the response as a string
            return response.toString();
        } catch (Exception e) {
            // Log the error and return the error message
            logger.severe("Error sending request to " + server + ":" + port + " - " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    // Get the headers of the request
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    // Get the body of the request
    public String getBody() {
        return body;
    }
}