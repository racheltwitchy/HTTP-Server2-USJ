package http.server.usj;

import java.util.ArrayList;
import java.io.Console;
import java.net.URL;

public class Client {

    public static void main(String[] args) {

        // Get the console object to interact with the user
        Console console = System.console();
        if (console == null) {
            System.err.println("No console available");
            return;
        }

        // Ask the user for the URL they want to connect to
        System.out.println("Enter the full URL you want to connect to (e.g., http://localhost:80): ");
        String urlString = console.readLine();
        String server = "";
        int port = 80; // Default HTTP port
        String path = "/"; // Default path

        // Add http:// to the URL if it is not present
        if (!urlString.startsWith("http://")) {
            urlString = "http://" + urlString;
        }

        try {
            // Parse the URL to extract the server, port, and path
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
            // Create a new request object
            Request request = new Request(server, port);
            ArrayList<String> validMethods = new ArrayList<>();
            validMethods.add("GET");
            validMethods.add("HEAD");
            validMethods.add("EXIT");

            // Check if the method is valid and list valid methods
            if (path.contains("/static")) {
                System.out.println("Type the HTTP method you want to use (GET, HEAD, EXIT): ");
            } else {
                validMethods.add("PUT");
                validMethods.add("POST");
                validMethods.add("DELETE");
                System.out.println("Type the HTTP method you want to use (GET, HEAD, PUT, POST, DELETE, EXIT): ");
            }

            // Read the method input from the user
            String method = console.readLine().toUpperCase();

            // Validate the method
            if (!validMethods.contains(method)) {
                System.out.println(ServerStatus.METHOD_NOT_ALLOWED_405.getStatusString());
                continue;
            } else if ("EXIT".equalsIgnoreCase(method)) {
                break; // Exit the loop if the method is EXIT
            }

            // Set the method and path for the request
            request.setMethod(method);
            request.setPath(path);

            // Collect headers from the user
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

            String body = "";

            // Collect body data based on the method type
            if ("POST".equals(method)) {
                System.out.println("Type the car details separated by commas (e.g., 'Toyota,Corolla,2015,20000'):");
                body = console.readLine();
                request.setBody(body);
            } else if ("PUT".equals(method)) {
                System.out.println(
                        "Type the car details to modify by an index separated by commas (e.g., '1,Toyota,Corolla,2015,20000'):");
                body = console.readLine();
                request.setBody(body);
            } else if ("DELETE".equals(method)) {
                System.out.println("Type the index of the car you want to delete:");
                body = console.readLine();
                request.setBody(body);
            } else if ("HEAD".equals(method) || "GET".equals(method)) {
                System.out.println("Type the body (if needed) and press enter: ");
                body = console.readLine();
                request.setBody(body);
            }

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
