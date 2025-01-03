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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kfnfnjlvdrngjrjkn.myshop.R;

import java.util.HashMap;

public class RegistorActivity extends AppCompatActivity {
    private Button registorButton;
    private EditText userTextName, userTextSurname, userTextPhone, userTextPassword, userTextEmail;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registor);

        registorButton = findViewById(R.id.register_button);
        userTextName = findViewById(R.id.user_name);
        userTextSurname = findViewById(R.id.user_surname);
        userTextPhone = findViewById(R.id.user_phone);
        userTextPassword = findViewById(R.id.user_password);
        userTextEmail = findViewById(R.id.user_email);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();

        registorButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        // Получение введенных данных
        String userName = userTextName.getText().toString().trim();
        String userSurname = userTextSurname.getText().toString().trim();
        String userPhone = userTextPhone.getText().toString().trim();
        String userPassword = userTextPassword.getText().toString().trim();
        String userEmail = userTextEmail.getText().toString().trim();

        // Проверка на пустые поля
        if (TextUtils.isEmpty(userName)) {
            showToast("Введите имя");
            return;
        }
        if (TextUtils.isEmpty(userSurname)) {
            showToast("Введите фамилию");
            return;
        }
        if (TextUtils.isEmpty(userPhone)) {
            showToast("Введите номер телефона");
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            showToast("Введите пароль");
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            showToast("Введите email");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // Аутентификация пользователя
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Получаем текущего пользователя
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();
                        saveUserData(userId, userName, userSurname, userPhone, userEmail);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showToast("Ошибка при регистрации: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserData(String userId, String username, String surname, String phone, String email) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> usersDataMap = new HashMap<>();
        usersDataMap.put("userId", userId);
        usersDataMap.put("name", username);
        usersDataMap.put("surname", surname);
        usersDataMap.put("phone", phone); // Сохраняем номер телефона
        usersDataMap.put("email", email); // Сохраняем адрес электронной почты

        rootRef.child("Users").child(phone).setValue(usersDataMap)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        showToast("Регистрация прошла успешно.");
                        Intent loginIntent = new Intent(RegistorActivity.this, ExitActivity.class);
                        startActivity(loginIntent);
                    } else {
                        showToast("Ошибка при сохранении данных: " + task.getException().getMessage());
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}