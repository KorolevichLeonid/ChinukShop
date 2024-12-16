package com.kfnfnjlvdrngjrjkn.myshop.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfnfnjlvdrngjrjkn.myshop.R;

import java.util.HashMap;

public class RegistorActivity extends AppCompatActivity {
    private Button registorButton;
    private EditText userTextName, userTextSurname, userTextLogin, userTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registor);


        registorButton = findViewById(R.id.register_button);
        userTextName = findViewById(R.id.user_name);
        userTextSurname = findViewById(R.id.user_surname);
        userTextLogin = findViewById(R.id.user_login);
        userTextPassword = findViewById(R.id.user_password);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);


        registorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        // Получение введенных данных
        String userName = userTextName.getText().toString();
        String userSurname = userTextSurname.getText().toString();
        String userLogin = userTextLogin.getText().toString();
        String userPassword = userTextPassword.getText().toString();


        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(userSurname)) {
            Toast.makeText(this, "Enter Surname", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(userLogin)) {
            Toast.makeText(this, "Enter Login", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE); // Показать прогресс-бар во время валидации
            ValidatePhone(userName, userSurname, userLogin, userPassword); // Валидация телефона
        }
    }

    private void ValidatePhone(String username, String surname, String phone, String password) {
        final DatabaseReference RootRef; // Ссылка на корень базы данных

        // Получение ссылки на корень базы данных Firebase
        RootRef = FirebaseDatabase.getInstance().getReference();

        // Создание слушателя для получения данных только один раз
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Проверка, существует ли пользователь с таким номером телефона
                if (!(dataSnapshot.child("Users").child(phone).exists())) {
                    // Если пользователя нет, создаем новый аккаунт
                    HashMap<String, Object> usersDataMap = new HashMap<>();
                    usersDataMap.put("phone", phone); // Добавляем номер телефона
                    usersDataMap.put("name", username); // Добавляем имя
                    usersDataMap.put("surname", surname); // Добавляем фамилию
                    usersDataMap.put("password", password); // Добавляем пароль

                    // Обновление данных в Firebase
                    RootRef.child("Users").child(phone).updateChildren(usersDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE); // Скрыть прогресс-бар
                                        Toast.makeText(RegistorActivity.this, "Регистрация прошла успешно.", Toast.LENGTH_SHORT).show();

                                        // Переход на экран входа
                                        Intent loginIntent = new Intent(RegistorActivity.this, ExitActivity.class);
                                        startActivity(loginIntent);
                                    } else {
                                        Toast.makeText(RegistorActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Если номер телефона уже зарегистрирован
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegistorActivity.this, "Номер " + phone + " уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    Intent ExitIntent = new Intent(RegistorActivity.this, ExitActivity.class);
                    startActivity(ExitIntent); // Переход на экран входа
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок
            }
        });
    }
}