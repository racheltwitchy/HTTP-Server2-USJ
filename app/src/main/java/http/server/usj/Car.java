package http.server.usj;

import java.util.Objects;

public class Car {
    String brand;
    String model;
    int horsePower;
    int price;

    public Car(String brand, String model, int horsePower, int price) {
        this.brand = brand;
        this.model = model;
        this.horsePower = horsePower;
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String toString() {
        return " Brand: " + brand + " Model: " + model + " Horse Power: " + horsePower + " Price: " + price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, horsePower, price);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Car other = (Car) obj;
        return this.brand.equals(other.brand) &&
                this.model.equals(other.model) &&
                this.horsePower == other.horsePower &&
                this.price == other.price;
    }

}