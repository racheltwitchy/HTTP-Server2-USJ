package http.server.usj;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    // Socket for communication with the client
    private final Socket socket;
    // List of Car objects to manage
    private final ArrayList<Car> cars;

    // Constructor to initialize ClientHandler with a socket and a list of cars
    public ClientHandler(Socket socket, ArrayList<Car> cars) {
        this.socket = socket;
        this.cars = cars;
        // Add default cars if the list is empty
        if (cars.isEmpty()) {
            cars.add(new Car("Toyota", "Corolla", 150, 20000));
            cars.add(new Car("Honda", "Civic", 160, 22000));
        }
    }

    // Override the run method to handle client requests
    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)) {

            // Read and build the request from the client
            StringBuilder requestBuilder = new StringBuilder();
            String line;
            while (!(line = input.readLine()).isBlank()) {
                requestBuilder.append(line).append("\r\n");
            }
            line = input.readLine(); // Read another line for possible body content or to complete the last request
                                     // line
            if (line != null && !line.isBlank()) {
                requestBuilder.append(line).append("\r\n");
            }

            // Split the request into lines
            String request = requestBuilder.toString();
            String[] requestLines = request.split("\r\n");
            String methodLine = requestLines[0];
            String[] methodLineParts = methodLine.split(" ");
            String method = methodLineParts[0]; // Extract the HTTP method
            String path = methodLineParts[1]; // Extract the path

            // Handle static content request
            if (path.contains("/static") || path.contains("/index.html")) {
                serveStaticContent(output, "app\\static\\index.html");
                return;
            }

            // Dynamic content handling based on HTTP method
            String[] handlers = new String[requestLines.length - 2];
            for (int i = 2; i < requestLines.length - 1; i++) {
                handlers[i - 2] = requestLines[i];
            }
            // Remove null and Content-Length headers
            handlers = removeNullValues(handlers);
            handlers = removeContentLength(handlers);
            String body = requestLines[requestLines.length - 1];
            String response = handleRequest(method, body, handlers);
            writer.print(response);
        } catch (Exception e) {
            System.out.println("Error handling client request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Remove null or blank values from an array
    private String[] removeNullValues(String[] array) {
        List<String> filteredList = Arrays.stream(array)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.toList());
        return filteredList.toArray(new String[0]);
    }

    // Remove Content-Length header from an array
    private String[] removeContentLength(String[] array) {
        List<String> filteredList = Arrays.stream(array)
                .filter(value -> !value.toLowerCase().startsWith("content-length"))
                .collect(Collectors.toList());
        return filteredList.toArray(new String[0]);
    }

    // Format headers array into a string with each header on a new line
    private String formatHeaders(String[] headers) {
        return Arrays.stream(headers).collect(Collectors.joining("\r\n"));
    }

    // Handle different HTTP methods
    private String handleRequest(String method, String body, String[] handlers) {
        switch (method) {
            case "GET":
                return handleGet(body, handlers);
            case "HEAD":
                return handleHead(handlers);
            case "POST":
                return handlePost(body, handlers);
            case "PUT":
                return handlePut(body, handlers);
            case "DELETE":
                return handleDelete(body, handlers);
            default:
                return ServerStatus.NOT_IMPLEMENTED_501.getStatusString() + "\r\n\r\n";
        }
    }

    // Handle GET request
    private String handleGet(String body, String[] handlers) {
        return ServerStatus.OK_200.getStatusString() + "\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                "Echoing back your request body(GET):\r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                formatHeaders(handlers) + "\r\n" +
                "Date: " + java.time.LocalDateTime.now() + "\r\n" +
                Server.carsToString(cars) + "\r\n";
    }

    // Handle HEAD request
    private String handleHead(String[] handlers) {
        return ServerStatus.OK_200.getStatusString() + "\r\nContent-Type: text/plain\r\n" + formatHeaders(handlers) +
                "\r\nConnection: close\r\n\r\n";
    }

    // Handle POST request
    private String handlePost(String body, String[] handlers) {
        // Example: Adding a new car to the list
        String[] parts = body.split(",");
        try {
            Car newCar = new Car(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            if (!cars.contains(newCar)) {
                cars.add(newCar);
                return ServerStatus.CREATED_201.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                        formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                        "Added car: " + newCar.toString();
            } else {
                return ServerStatus.CONFLICT_409.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                        formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                        "Car already exists";
            }
        } catch (Exception e) {
            return ServerStatus.BAD_REQUEST_400.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                    formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                    "Invalid car data format";
        }
    }

    // Handle PUT request
    private String handlePut(String body, String[] handlers) {
        // Modify a car by an index passed in the body
        String[] parts = body.split(",");
        try {
            int index = Integer.parseInt(parts[0].trim()) - 1;
            if (index >= 0 && index < cars.size()) {
                Car oldCar = cars.get(index);
                Car modifiedCar = new Car(parts[1], parts[2], Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                cars.set(index, modifiedCar);
                return ServerStatus.OK_200.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                        formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                        "Old car: " + oldCar.toString() + "\r\n" +
                        "Modified car: " + modifiedCar.toString();
            } else {
                return ServerStatus.NOT_FOUND_404.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                        formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                        "Car index out of bounds";
            }
        } catch (Exception e) {
            return ServerStatus.BAD_REQUEST_400.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                    formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                    "Invalid car data format";
        }
    }

    // Handle DELETE request
    private String handleDelete(String body, String[] handlers) {
        // Example: Removing a car by index
        try {
            int index = Integer.parseInt(body.trim()) - 1;
            if (index >= 0 && index < cars.size()) {
                Car removedCar = cars.remove(index);
                return ServerStatus.OK_200.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                        formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                        "Deleted car: " + removedCar.toString();
            } else {
                return ServerStatus.NOT_FOUND_404.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                        formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                        "Car index out of bounds";
            }
        } catch (NumberFormatException e) {
            return ServerStatus.BAD_REQUEST_400.getStatusString() + "\r\nContent-Type: text/plain\r\n" +
                    formatHeaders(handlers) + "\r\nConnection: close\r\n\r\n" +
                    "Invalid index format";
        }
    }

    // Serve static content (e.g., HTML files)
    private void serveStaticContent(OutputStream output, String filePath) {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(output)) {
            // Write HTTP Headers with OK status and content type
            PrintWriter pw = new PrintWriter(bos, true);
            pw.println(ServerStatus.OK_200.getStatusString());
            pw.println("Content-Type: text/html");
            pw.println("Content-Length: " + file.length());
            pw.println(""); // Blank line between headers and content, very important!
            pw.flush();

            // Stream file contents
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, count);
            }
            bos.flush();
        } catch (IOException e) {
            System.out.println("Error serving static content: " + e.getMessage());
        }
    }
}
