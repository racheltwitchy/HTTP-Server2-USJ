package http.server.usj;

public class Client {
    public static void main(String[] args) {
        System.out.println("Type the server you want to connect to: ");
        String server = System.console().readLine();

        System.out.println("Type the port you want to connect to: ");
        int port = Integer.parseInt(System.console().readLine());

        while (true) {
            Request request = new Request(server, port);  // Create a new Request object each iteration

            System.out.println("Type the HTTP method you want to use (GET, HEAD, PUT, POST, DELETE, EXIT): ");
            String method = System.console().readLine();
            
            if ("EXIT".equalsIgnoreCase(method)) {
                break;  // Exit the loop if the method is EXIT
            }

            request.setMethod(method);

            // Collecting headers from the user
            System.out.println("Enter headers (type 'Name: Value'), type 'STOP' to finish:");
            while (true) {
                String headerLine = System.console().readLine();
                if ("STOP".equalsIgnoreCase(headerLine)) {
                    break;  // Stop adding headers
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
            String body = System.console().readLine();
            request.setBody(body);

            // Print the full request details for review before sending
            System.out.println("\nFinal Request Details:");
            System.out.println("Method: " + method);
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
