package com.kfnfnjlvdrngjrjkn.myshop.Users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.kfnfnjlvdrngjrjkn.myshop.UI.ExitActivity;

public class SelectPasswordActivity extends AppCompatActivity {
    private EditText newPasswordInput;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;
    private String actionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_password);

        newPasswordInput = findViewById(R.id.new_password_input);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        mAuth = FirebaseAuth.getInstance();

        // Получение данных из интента
        if (getIntent() != null && getIntent().getData() != null) {
            Uri data = getIntent().getData();
            String fullUrl = data.toString();
            Log.d("SelectPasswordActivity", "Full URL: " + fullUrl);

            String mode = data.getQueryParameter("mode");
            actionCode = data.getQueryParameter("oobCode");

            Log.d("SelectPasswordActivity", "Mode: " + mode);
            Log.d("SelectPasswordActivity", "oobCode: " + actionCode);

            if (TextUtils.isEmpty(actionCode) || !TextUtils.equals(mode, "resetPassword")) {
                Toast.makeText(this, "Ошибка: неверные параметры ссылки", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Ошибка: отсутствует код действия", Toast.LENGTH_SHORT).show();
            finish();
        }

        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = newPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Введите новый пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("SelectPasswordActivity", "Код действия: " + actionCode);
        Log.d("SelectPasswordActivity", "Новый пароль: " + newPassword);

        if (TextUtils.isEmpty(actionCode)) {
            Log.e("SelectPasswordActivity", "Код действия отсутствует");
            Toast.makeText(this, "Ошибка: отсутствует код действия", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.confirmPasswordReset(actionCode, newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SelectPasswordActivity.this, "Пароль успешно изменен!", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(SelectPasswordActivity.this, ExitActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        Log.e("SelectPasswordActivity", "Ошибка при изменении пароля", task.getException());
                        Toast.makeText(SelectPasswordActivity.this, "Ошибка при изменении пароля: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Неизвестная ошибка"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}