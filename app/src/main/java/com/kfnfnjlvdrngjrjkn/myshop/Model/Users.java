package com.kfnfnjlvdrngjrjkn.myshop.Model;

import java.util.ArrayList;
import java.util.List;

public class Users {

    private String phone;
    private String password;
    private String trainingDate;
    private String trainingTime;
    private String selectedTrainerName;
    private String bookingConfirmation;
    private List<String> cartItems;
    private String name;
    private String email;
    private String address;
    private String image;

    public Users() {
        // Пустой конструктор необходим для Firebase
        this.cartItems = new ArrayList<>(); // Инициализация списка
    }

    // Геттеры и сеттеры для полей
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public String getTrainingTime() {
        return trainingTime;
    }

    public void setTrainingTime(String trainingTime) {
        this.trainingTime = trainingTime;
    }

    public String getSelectedTrainerName() {
        return selectedTrainerName;
    }

    public void setSelectedTrainerName(String selectedTrainerName) {
        this.selectedTrainerName = selectedTrainerName;
    }

    public String getBookingConfirmation() {
        return bookingConfirmation;
    }

    public void setBookingConfirmation(String bookingConfirmation) {
        this.bookingConfirmation = bookingConfirmation;
    }

    // Методы для управления корзиной
    public List<String> getCartItems() {
        return cartItems;
    }

    public void addToCart(String itemId) {
        if (!cartItems.contains(itemId)) {
            cartItems.add(itemId);
        }
    }

    public void removeFromCart(String itemId) {
        cartItems.remove(itemId);
    }

    public void clearCart() {
        cartItems.clear();
    }

    // Геттеры и сеттеры для дополнительных полей
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}