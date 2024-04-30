package http.server.usj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private String server;
    private int port;
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private String body = "";

    public Request(String server, int port) {
        this.server = server;
        this.port = port;
        this.path = "/";
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        if(method.equals("GET") ){
            this.path = path;
        }
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

            out.println(request.toString());
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
    
}
