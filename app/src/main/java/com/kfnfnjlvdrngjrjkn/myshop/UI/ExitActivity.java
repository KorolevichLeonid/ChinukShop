package com.kfnfnjlvdrngjrjkn.myshop.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import com.google.android.material.checkbox.MaterialCheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfnfnjlvdrngjrjkn.myshop.Admin.AdminCategoryActivity;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;
import com.kfnfnjlvdrngjrjkn.myshop.Prevalent.Prevalent;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.kfnfnjlvdrngjrjkn.myshop.Users.HomeActivity;

import io.paperdb.Paper;

public class ExitActivity extends AppCompatActivity {
    private Button exitButton;
    private EditText userTextLogin, userTextPassword;
    private ProgressBar progressBar;
    private MaterialCheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);


        exitButton = findViewById(R.id.exit_button);
        userTextLogin = findViewById(R.id.user_login);
        userTextPassword = findViewById(R.id.user_password);
        checkBoxRememberMe = findViewById(R.id.remember_me_checkbox);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);
        Paper.init(this); // для локального хранения

        // Проверка сохраненных данных для автоматического входа
        String savedPhone = Paper.book().read(Prevalent.getUserPhoneKey());
        String savedPassword = Paper.book().read(Prevalent.getUserPasswordKey());

        // Если сохраненные данные не пустые, попытка входа
        if (!TextUtils.isEmpty(savedPhone) && !TextUtils.isEmpty(savedPassword)) {
            progressBar.setVisibility(View.VISIBLE);
            validateUser(savedPhone, savedPassword);
        }


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        // Получение введенных данных
        String userLogin = userTextLogin.getText().toString();
        String userPassword = userTextPassword.getText().toString();

        // Проверка на пустые поля
        if (TextUtils.isEmpty(userLogin)) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE); // Показать прогресс-бар
            validateUser(userLogin, userPassword); // Валидация пользователя
        }
    }

    private void validateUser(String phone, String password) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(); // Ссылка на корень базы данных

        // Получение данных из Firebase
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Проверка администратора
                if (dataSnapshot.child("Admin").child(phone).exists()) {
                    Users adminData = dataSnapshot.child("Admin").child(phone).getValue(Users.class);

                    // Проверка пароля администратора
                    if (adminData != null && adminData.getPassword().equals(password)) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ExitActivity.this, "Добро пожаловать, администратор!", Toast.LENGTH_SHORT).show();
                        Intent adminIntent = new Intent(ExitActivity.this, AdminCategoryActivity.class);
                        startActivity(adminIntent);
                        finish(); // Завершение текущей активности
                        return;
                    }
                }

                // Проверка обычного пользователя
                if (dataSnapshot.child("Users").child(phone).exists()) {
                    Users userData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    // Проверка пароля пользователя
                    if (userData != null && userData.getPassword().equals(password)) {
                        if (checkBoxRememberMe.isChecked()) {
                            // Сохранение данных в Paper для автоматического входа
                            Paper.book().write(Prevalent.getUserPhoneKey(), phone);
                            Paper.book().write(Prevalent.getUserPasswordKey(), password);
                        }


                        Prevalent.setCurrentOnlineUser(userData); // Важно!

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ExitActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                        Intent homeIntent = new Intent(ExitActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish(); // Завершение текущей активности
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ExitActivity.this, "Неверный номер телефона или пароль", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ExitActivity.this, "Аккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent exitIntent = new Intent(ExitActivity.this, RegistorActivity.class);
                    startActivity(exitIntent);
                    finish(); // Завершение текущей активности
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ExitActivity.this, "Ошибка при получении данных: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}