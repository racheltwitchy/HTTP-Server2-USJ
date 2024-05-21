package http.server.usj;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    // Main method to start the server
    public static void main(String[] args) {
        // Create an ArrayList to store Car objects
        ArrayList<Car> cars = new ArrayList<>();
        // Prompt the user to enter the port number
        System.out.println("Type the port you want: ");
        String port = System.console().readLine();

        // Try to open a ServerSocket on the specified port
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
            System.out.println("Server is listening on port " + port);

            // Infinite loop to continuously accept new client connections
            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                // Start a new thread to handle the client connection
                new Thread(new ClientHandler(socket, cars)).start();
            }
        } catch (Exception e) {
            // Handle exceptions that occur while running the server
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to convert the cars ArrayList to a string with an index for each car
    public static String carsToString(ArrayList<Car> cars) {
        // Initialize an empty result string
        String res = "";
        // Iterate through the cars list and append each car's details to the result
        // string
        for (int i = 1; i <= cars.size(); i++) {
            res += "Car: " + i + cars.get(i - 1).toString() + "\n";
        }
        // Return the result string
        return res;
    }
}