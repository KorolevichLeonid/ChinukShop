package com.kfnfnjlvdrngjrjkn.myshop.Prevalent;

import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;

public class Prevalent {
    private static Users currentOnlineUser;

    public static final String UserPhoneKey = "UserPhone";
    public static final String UserPasswordKey = "UserPassword";

    // Метод для установки текущего пользователя
    public static void setCurrentOnlineUser(Users user) {
        currentOnlineUser = user;
    }

    // Метод для получения текущего пользователя
    public static Users getCurrentOnlineUser() {
        return currentOnlineUser;
    }

    public static String getUserPhoneKey() {
        return UserPhoneKey;
    }

    public static String getUserPasswordKey() {
        return UserPasswordKey;
    }
}