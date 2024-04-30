package http.server.usj;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private List<Car> cars;

    public static void main(String[] args) {
        ArrayList<Car> cars = new ArrayList<>();
        System.out.println("Type the port you want: ");
        String port = System.console().readLine();

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                // Start a new thread for each connection
                new Thread(new ClientHandler(socket, cars)).start();
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

    public List<Car> getCars() {
        return cars;
    }

    public Car getCarById(int id) {
        if (id >= 0 && id < cars.size()) {
            return cars.get(id);
        }
        return null;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void updateCar(int id, Car car) {
        if (id >= 0 && id < cars.size()) {
            cars.set(id, car);
        }
    }

    public void deleteCar(int id) {
        if (id >= 0 && id < cars.size()) {
            cars.remove(id);
        }
    }
}