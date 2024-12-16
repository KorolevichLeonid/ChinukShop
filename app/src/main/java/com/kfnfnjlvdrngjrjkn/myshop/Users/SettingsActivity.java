package com.kfnfnjlvdrngjrjkn.myshop.Users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;
import com.kfnfnjlvdrngjrjkn.myshop.Prevalent.Prevalent;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView saveTextButton, closeTextBtn;
    private Uri imageUri;
    private String currentUserId; // ID текущего пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        profileImageView = findViewById(R.id.settings_account_image);
        fullNameEditText = findViewById(R.id.settings_fullname);
        addressEditText = findViewById(R.id.settings_address);
        saveTextButton = findViewById(R.id.save_settings_tv);
        closeTextBtn = findViewById(R.id.close_settings_tv);

        // Получаем текущего вошедшего пользователя
        Users currentUser = Prevalent.getCurrentOnlineUser();
        currentUserId = currentUser.getPhone();

        // Заполняем поля

        fullNameEditText.setText(currentUser.getName());
        addressEditText.setText(currentUser.getAddress());
        if (currentUser.getImage() != null) {
            Picasso.get().load(currentUser.getImage()).into(profileImageView);
        }

        closeTextBtn.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, HomeActivity.class)));

        saveTextButton.setOnClickListener(view -> updateUserInfo());

        profileImageView.setOnClickListener(view -> openGallery());
    }

    private void updateUserInfo() {
        String name = fullNameEditText.getText().toString();
        String address = addressEditText.getText().toString();

        // Проверка
        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        // Загружаем текущие данные пользователя
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                HashMap<String, Object> userMap = new HashMap<>();

                if (snapshot.exists()) {
                    // Обновляем изм поля
                    userMap.put("name", name);
                    userMap.put("address", address);

                    // сохраняем изображение в локальную папку
                    if (imageUri != null) {
                        String localImagePath = saveImageLocally(imageUri); // Метод для сохранения изображения
                        userMap.put("image", localImagePath); // Сохраняем локальный путь как строку
                    }

                    ref.updateChildren(userMap).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Информация успешно обновлена", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Ошибка обновления информации", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SettingsActivity.this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String saveImageLocally(Uri imageUri) {
        try {
            // Получаем имя файла и создаем путь в локальной папке
            String fileName = System.currentTimeMillis() + ".jpg";
            File directory = new File(getExternalFilesDir(null), "ProfileImages");
            if (!directory.exists()) {
                directory.mkdirs(); // Создание директории, если она не существует
            }
            File imageFile = new File(directory, fileName);

            // Копируем изображение в локальную папку
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return imageFile.getAbsolutePath(); // Возвращаем полный путь к файлу
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}