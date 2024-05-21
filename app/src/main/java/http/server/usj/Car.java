package http.server.usj;

import java.util.Objects;

public class Car {
    // Fields representing car properties
    String brand;
    String model;
    int horsePower;
    int price;

    // Constructor to initialize a new Car object with specified properties
    public Car(String brand, String model, int horsePower, int price) {
        this.brand = brand;
        this.model = model;
        this.horsePower = horsePower;
        this.price = price;
    }

    // Getter method for brand
    public String getBrand() {
        return brand;
    }

    // Setter method for brand
    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Getter method for model
    public String getModel() {
        return model;
    }

    // Setter method for model
    public void setModel(String model) {
        this.model = model;
    }

    // Getter method for horse power
    public int getHorsePower() {
        return horsePower;
    }

    // Setter method for horse power
    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }

    // Getter method for price
    public int getPrice() {
        return price;
    }

    // Setter method for price
    public void setPrice(int price) {
        this.price = price;
    }

    // Override the toString method to provide a string representation of the Car
    // object
    @Override
    public String toString() {
        return "Brand: " + brand + " Model: " + model + " Horse Power: " + horsePower + " Price: " + price;
    }

    // Override the hashCode method to generate a hash code based on car properties
    @Override
    public int hashCode() {
        return Objects.hash(brand, model, horsePower, price);
    }

    // Override the equals method to compare two Car objects for equality
    @Override
    public boolean equals(Object obj) {
        // Check if the objects are the same
        if (this == obj)
            return true;
        // Check if the other object is null or of a different class
        if (obj == null || getClass() != obj.getClass())
            return false;
        // Cast the other object to Car and compare properties
        Car other = (Car) obj;
        return this.brand.equals(other.brand) &&
                this.model.equals(other.model) &&
                this.horsePower == other.horsePower &&
                this.price == other.price;
    }
}