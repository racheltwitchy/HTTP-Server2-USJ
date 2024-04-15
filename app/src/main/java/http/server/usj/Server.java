package http.server.usj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        ArrayList<Car> cars = new ArrayList<Car>();
        String response = "";

        System.out.println("Type the port you want: ");
        String port = System.console().readLine();

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        OutputStream output = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true)) {

                    StringBuilder requestBuilder = new StringBuilder();
                    String line;
                    while (!(line = input.readLine()).isBlank()) {
                        requestBuilder.append(line + "\r\n");
                    }

                    String request = requestBuilder.toString();
                    String method = request.split(" ")[0];
                    // int bodyIndex = request.indexOf("\r\n\r\n"); Doesnt work properly
                    // String body = request.substring(bodyIndex + 4); // Skip the "\r\n\r\n"
                    String body = request.split("\r\n")[request.split("\r\n").length - 1];
                    if (method.equals("GET")) {
                        response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n" +
                                "Echoing back your request body(GET):\r\n"
                                + "Content-Length: " + body.getBytes().length + " \r\n"
                                + "Date: " + java.time.LocalDateTime.now() + "\r\n"
                                + Server.carsToString(cars)
                                + "\r\n";
                    }
                    if (method.equals("HEAD")) {
                        response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n"
                                + "Date: " + java.time.LocalDateTime.now() + "\r\n";
                    }
                    if (method.equals("PUT")) {
                        String[] parts = body.split(",");
                        Car temp = new Car(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

                        if (!cars.contains(temp)) {
                            cars.add(temp);
                            response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n"
                                    + "Date: " + java.time.LocalDateTime.now() + "\r\n"
                                    + "Added car: " + temp.toString();
                        } else {
                            response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n"
                                    + "Date: " + java.time.LocalDateTime.now() + "\r\n"
                                    + "The car already exists";
                        }
                    }
                    if (method.equals("POST")) {
                        String[] numbers = body.split(" ");
                        double min = Double.min(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
                        double max = Double.max(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
                        ArrayList<Car> temp = new ArrayList<Car>();
                        for (Car c : cars) {
                            if (c.price >= min && c.price <= max) {
                                temp.add(c);
                            }
                        }
                        response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n"
                                + "Date: " + java.time.LocalDateTime.now() + "\r\n"
                                + "The car between the prices (" + min + " y " + max + ") are: \r\n"
                                + temp.toString();
                    }
                    if (method.equals("DELETE")) {
                        try {
                            int index = Integer.parseInt(body); // Asumiendo que body contiene un número de índice
                                                                // válido.
                            if (index >= 1 && index <= cars.size()) {
                                Car temp = cars.remove(index - 1); // Elimina y devuelve el coche en el índice
                                                                   // especificado.
                                response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n"
                                        + "Date: " + java.time.LocalDateTime.now() + "\r\n"
                                        + "Deleted car: " + temp.toString();
                            } else {
                                response = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n"
                                        + "Date: " + java.time.LocalDateTime.now() + "\r\n"
                                        + "The car index is out of bounds";
                            }
                        } catch (NumberFormatException e) {
                            response = "HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\nConnection: close\r\n\r\n"
                                    + "Date: " + java.time.LocalDateTime.now() + "\r\n"
                                    + "Invalid index format";
                        }
                    }
                    writer.print(response);
                }
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to convert the cars arraylist to a string and have an index for each
    // car
    public static String carsToString(ArrayList<Car> cars) {

        String res = "";
        for (int i = 1; i <= cars.size(); i++) {
            res += "Car: " + i + cars.get(i - 1).toString() + "\n";
        }
        return res;
    }
}