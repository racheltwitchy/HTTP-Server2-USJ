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

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ArrayList<Car> cars;

    public ClientHandler(Socket socket, ArrayList<Car> cars) {
        this.socket = socket;
        this.cars = cars;
        if(cars.isEmpty()){
            cars.add(new Car("Toyota", "Corolla", 150, 20000));
            cars.add(new Car("Honda", "Civic", 160, 22000));
        }
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {

            StringBuilder requestBuilder = new StringBuilder();
            String line;
            while (!(line = input.readLine()).isBlank()) {
                requestBuilder.append(line + "\r\n");
            }
            line = input.readLine(); // Read another line for possible body content or to complete the last request line
            if (line != null && !line.isBlank()) {
                requestBuilder.append(line + "\r\n");
            }

            String request = requestBuilder.toString();
            String[] requestLines = request.split("\r\n");
            String methodLine = requestLines[0];
            String[] methodLineParts = methodLine.split(" ");
            String method = methodLineParts[0];
            String path = methodLineParts[1];

            // Handle static content request
            if (path.contains("/static") || path.contains("/index.html")) {
                serveStaticContent(output, "app\\static\\index.html");
                return;
            }

            // Dynamic content handling based on HTTP method
            String body = requestLines[requestLines.length - 1];
            String response = handleRequest(method, body);
            writer.print(response);
        } catch (Exception e) {
            System.out.println("Error handling client request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String handleRequest(String method, String body) {
        switch (method) {
            case "GET":
                return handleGet(body);
            case "HEAD":
                return handleHead();
            case "POST":
                return handlePost(body);
            case "PUT":
                return handlePut(body);
            case "DELETE":
                return handleDelete(body);
            default:
                return "HTTP/1.1 501 Not Implemented\r\n\r\n";
        }
    }

    private String handleGet(String body) {
        return "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                "Echoing back your request body(GET):\r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "Date: " + java.time.LocalDateTime.now() + "\r\n" +
                Server.carsToString(cars) + "\r\n";
    }

    private String handleHead() {
        return "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n";
    }

    private String handlePost(String body) {
        // Example: Parsing input to find cars in a price range
        String[] parts = body.split(",");
        try {
            double min = Double.parseDouble(parts[0]);
            double max = Double.parseDouble(parts[1]);
            return filterCarsByPrice(min, max);
        } catch (NumberFormatException e) {
            return "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                   "Invalid price format";
        }
    }

    private String handlePut(String body) {
        // Example: Adding a new car to the list
        String[] parts = body.split(",");
        try {
            Car newCar = new Car(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            if (!cars.contains(newCar)) {
                cars.add(newCar);
                return "HTTP/1.1 201 Created\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                       "Added car: " + newCar.toString();
            } else {
                return "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                       "Car already exists";
            }
        } catch (Exception e) {
            return "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                   "Invalid car data format";
        }
    }

    private String handleDelete(String body) {
        // Example: Removing a car by index
        try {
            int index = Integer.parseInt(body.trim()) - 1;
            if (index >= 0 && index < cars.size()) {
                Car removedCar = cars.remove(index);
                return "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                       "Deleted car: " + removedCar.toString();
            } else {
                return "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                       "Car index out of bounds";
            }
        } catch (NumberFormatException e) {
            return "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                   "Invalid index format";
        }
    }

    private String filterCarsByPrice(double min, double max) {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n");
        sb.append("Cars in price range: ").append(min).append(" to ").append(max).append("\r\n");
        for (Car car : cars) {
            if (car.getPrice() >= min && car.getPrice() <= max) {
                sb.append(car.toString()).append("\r\n");
            }
        }
        return sb.toString();
    }

    private void serveStaticContent(OutputStream output, String filePath) {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(output)) {
            // Write HTTP Headers with OK status and content type
            PrintWriter pw = new PrintWriter(bos, true);
            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-Type: text/html");
            pw.println("Content-Length: " + file.length());
            pw.println(""); // blank line between headers and content, very important!
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
