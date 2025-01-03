package com.kfnfnjlvdrngjrjkn.myshop.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Импортируем для логирования
import android.view.View;
import android.widget.Button;
import com.google.android.material.checkbox.MaterialCheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;
import com.kfnfnjlvdrngjrjkn.myshop.Prevalent.Prevalent;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.kfnfnjlvdrngjrjkn.myshop.Users.HomeActivity;
import com.kfnfnjlvdrngjrjkn.myshop.Users.ResetPasswordActivity;
import com.kfnfnjlvdrngjrjkn.myshop.Admin.AdminCategoryActivity; // Импортируем активити для администратора

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
        TextView forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(v -> {
            Intent resetPasswordIntent = new Intent(ExitActivity.this, ResetPasswordActivity.class);
            startActivity(resetPasswordIntent);
        });

        Paper.init(this);
        checkForSavedCredentials();

        exitButton.setOnClickListener(v -> createAccount());
    }

    private void checkForSavedCredentials() {
        String savedPhone = Paper.book().read(Prevalent.getUserPhoneKey());
        String savedPassword = Paper.book().read(Prevalent.getUserPasswordKey());

        if (!TextUtils.isEmpty(savedPhone) && !TextUtils.isEmpty(savedPassword)) {
            progressBar.setVisibility(View.VISIBLE);
            validateUser(savedPhone, savedPassword);
        }
    }

    private void createAccount() {
        String userLogin = userTextLogin.getText().toString();
        String userPassword = userTextPassword.getText().toString();

        if (areFieldsEmpty(userLogin, userPassword)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        validateUser(userLogin, userPassword);
    }

    private boolean areFieldsEmpty(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            showToast("Введите номер телефона");
            return true;
        } else if (TextUtils.isEmpty(password)) {
            showToast("Введите пароль");
            return true;
        }
        return false;
    }

    private void validateUser(String phone, String password) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Логирование для отладки
        Log.d("ExitActivity", "Checking user with phone: " + phone);

        rootRef.child("Users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users userData = dataSnapshot.getValue(Users.class);

                    // Логирование для отладки
                    Log.d("ExitActivity", "User data found: " + (userData != null ? userData.getName() : "null"));

                    if (userData != null && userData.getPhone().equals(phone)) {
                        // Проверяем, является ли пользователь администратором
                        if (userData.getPhone().equals("12345")) {
                            Intent adminIntent = new Intent(ExitActivity.this, AdminCategoryActivity.class);
                            startActivity(adminIntent);
                            finish();
                        } else {
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(userData.getEmail(), password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            handleSuccessfulLogin(phone, password, userData);
                                        } else {
                                            handleFailedLogin("Неверный номер телефона или пароль");
                                        }
                                    });
                        }
                    } else {
                        handleFailedLogin("Аккаунт с номером " + phone + " не существует");
                    }
                } else {
                    handleFailedLogin("Аккаунт с номером " + phone + " не существует");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleFailedLogin("Ошибка при получении данных: " + error.getMessage());
            }
        });
    }

    private void handleSuccessfulLogin(String phone, String password, Users userData) {
        if (checkBoxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.getUserPhoneKey(), phone);
            Paper.book().write(Prevalent.getUserPasswordKey(), password);
        }

        Prevalent.setCurrentOnlineUser(userData);
        progressBar.setVisibility(View.GONE);
        showToast("Успешный вход");
        Intent homeIntent = new Intent(ExitActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void handleFailedLogin(String message) {
        progressBar.setVisibility(View.GONE);
        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}