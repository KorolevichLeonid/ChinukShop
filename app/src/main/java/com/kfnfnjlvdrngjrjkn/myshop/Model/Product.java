package com.kfnfnjlvdrngjrjkn.myshop.Model;

public class Product {
    private String pid;          // Уникальный идентификатор товара
    private String name;         // Название товара
    private String description;  // Описание товара
    private double price;        // Цена товара
    private String image;        // Путь к изображению товара

    // Пустой конструктор нужен для Firebase
    public Product() {}

    // Конструктор с параметрами
    public Product(String name, String image, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    // Геттеры и сеттеры
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Product{" +
                "pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                '}';
    }
}