package http.server.usj;

import java.util.ArrayList;
import java.io.Console;
import java.net.URL;

public class Client {

    public static void main(String[] args) {

        Console console = System.console();
        if (console == null) {
            System.err.println("No console available");
            return;
        }

        System.out.println("Enter the full URL you want to connect to (e.g., http://localhost:80): ");
        String urlString = console.readLine();
        String server = "";
        int port = 80; // Default HTTP port
        String path = "/"; // Default path

        // Add http:// if not present
        if (!urlString.startsWith("http://")) {
            urlString = "http://" + urlString;
        }

        try {
            URL url = new URL(urlString);
            server = url.getHost();
            port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
            path = url.getPath().isEmpty() ? "/" : url.getPath();
        } catch (Exception e) {
            System.err.println("Invalid URL. Please make sure the URL is correct.");
            e.printStackTrace();
            return;
        }

        while (true) {
            Request request = new Request(server, port);
            ArrayList<String> validMethods = new ArrayList<String>();
            validMethods.add("GET");
            validMethods.add("HEAD");
            validMethods.add("EXIT");
            // Check if the method is valid
            if (path.contains("/static")) {
                System.out.println("Type the HTTP method you want to use (GET, HEAD, EXIT): ");
            } else {
                validMethods.add("PUT");
                validMethods.add("POST");
                validMethods.add("DELETE");
                System.out.println("Type the HTTP method you want to use (GET, HEAD, PUT, POST, DELETE, EXIT): ");
            }
            String method = console.readLine();

            if (!validMethods.contains(method)) {
                System.out.println(ServerStatus.METHOD_NOT_ALLOWED_405.getStatusString());
                continue;
            } else if ("EXIT".equalsIgnoreCase(method)) {
                break; // Exit the loop if the method is EXIT
            }

            request.setMethod(method);
            request.setPath(path); // Set path extracted from URL

            // Collecting headers from the user
            System.out.println("Enter headers (type 'Name: Value'), type 'STOP' to finish:");
            while (true) {
                String headerLine = console.readLine();
                if ("STOP".equalsIgnoreCase(headerLine)) {
                    break; // Stop adding headers
                }
                String[] parts = headerLine.split(": ", 2);
                if (parts.length == 2) {
                    request.addHeader(parts[0], parts[1]);
                } else {
                    System.out.println("Invalid header format. Please enter in 'Name: Value' format.");
                }
            }

            // Input the body of the message if necessary
            System.out.println("Type the body (if needed) and press enter: ");
            String body = console.readLine();
            request.setBody(body);

            // Print the full request details for review before sending
            System.out.println("\nFinal Request Details:");
            System.out.println("Method: " + method);
            System.out.println("URL: " + urlString);
            System.out.println("Headers:");
            request.getHeaders().forEach((key, value) -> System.out.println(key + ": " + value));
            if (!body.isEmpty()) {
                System.out.println("Body: " + body);
            }
            System.out.println("\nSending request...\n");

            // Send the request and receive the response
            String response = request.send();
            System.out.println("Response from server:\n" + response);
        }
    }
}
