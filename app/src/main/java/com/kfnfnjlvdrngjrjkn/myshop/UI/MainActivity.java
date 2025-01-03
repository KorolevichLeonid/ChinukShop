package com.kfnfnjlvdrngjrjkn.myshop.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;
import com.kfnfnjlvdrngjrjkn.myshop.Prevalent.Prevalent;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.kfnfnjlvdrngjrjkn.myshop.Users.HomeActivity;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button exitButton, loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        exitButton = (Button) findViewById(R.id.main_exit_btn);
        loginButton = (Button) findViewById(R.id.registor_btn);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Paper.init(this); //для хранения данных
        progressBar.setVisibility(View.GONE); //


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(MainActivity.this, RegistorActivity.class);
                startActivity(LoginIntent);
            }
        });


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ExitIntent = new Intent(MainActivity.this, ExitActivity.class);
                startActivity(ExitIntent);
            }
        });

        // Получение сохраненных данных пользователя
        String userPhoneKey = Paper.book().read(Prevalent.getUserPhoneKey());
        String userPasswordKey = Paper.book().read(Prevalent.getUserPasswordKey());

        // Если данные не пустые, попытка автоматического входа
        if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {
            progressBar.setVisibility(View.VISIBLE); // Показать прогресс-бар
            ValidateUser(userPhoneKey, userPasswordKey); // Валидация пользователя
        }
    }

    private void ValidateUser(final String phone, final String password) {
        final DatabaseReference RootRef; // Ссылка на корень базы данных
        RootRef = FirebaseDatabase.getInstance().getReference();

        // Получение данных из Firebase
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Проверка, существует ли пользователь с таким номером телефона
                if (dataSnapshot.child("Users").child(phone).exists()) {
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    // Проверка телефона и пароля пользователя
                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();

                            Intent HomeIntent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(HomeIntent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } else {
                    progressBar.setVisibility(View.GONE); // Скрыть прогресс-бар
                    Toast.makeText(MainActivity.this, "Аккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent exitIntent = new Intent(MainActivity.this, RegistorActivity.class);
                    startActivity(exitIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}