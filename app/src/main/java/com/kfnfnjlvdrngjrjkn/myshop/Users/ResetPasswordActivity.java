package com.kfnfnjlvdrngjrjkn.myshop.Users;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kfnfnjlvdrngjrjkn.myshop.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText emailInput;
    private Button resetButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailInput = findViewById(R.id.email_input);
        resetButton = findViewById(R.id.reset_button);
        mAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = emailInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Введите адрес электронной почты", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Письмо для сброса пароля отправлено!", Toast.LENGTH_SHORT).show();
                        finish(); // Закрыть активность после успешного отправления
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Ошибка при отправке письма: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}