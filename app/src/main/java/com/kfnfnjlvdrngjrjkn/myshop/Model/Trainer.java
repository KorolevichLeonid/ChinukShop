package com.kfnfnjlvdrngjrjkn.myshop.Model;

public class Trainer {
    private String trainerId;
    private String name; // Имя тренера
    private String specialty;
    private String imageUrl;

    public Trainer() {
        // Пустой конструктор необходим для Firebase
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getName() { // Переименовано для ясности
        return name;
    }

    public void setName(String name) { // Переименовано для ясности
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}