package com.kfnfnjlvdrngjrjkn.myshop.Admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewTrainerActivity extends AppCompatActivity {

    private static final int GALLERYPICK = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private String trainerId, trainerName, specialty, saveCurrentDate, saveCurrentTime;
    private String localImagePath;
    private ImageView trainerImage;
    private EditText trainerNameInput, specialtyInput;
    private Button addTrainerButton;
    private Uri imageUri;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_trainer);

        init();

        if (!checkStoragePermission()) {
            requestStoragePermission();
        }

        trainerImage.setOnClickListener(view -> openGallery());

        addTrainerButton.setOnClickListener(view -> validateTrainerData());
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение на доступ к хранилищу предоставлено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Разрешение на доступ к хранилищу отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateTrainerData() {
        trainerName = trainerNameInput.getText().toString().trim();
        specialty = specialtyInput.getText().toString().trim();

        if (TextUtils.isEmpty(trainerName)) {
            Toast.makeText(this, "Добавьте имя тренера.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(specialty)) {
            Toast.makeText(this, "Добавьте специальность тренера.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Добавьте изображение тренера.", Toast.LENGTH_SHORT).show();
            return;
        }

        storeTrainerInformation();
    }

    private void storeTrainerInformation() {
        loadingBar.setTitle("Сохранение данных");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());
        trainerId = saveCurrentDate + saveCurrentTime;

        saveImageToFile();
    }

    private void saveImageToFile() {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File imageFile = new File(directory, trainerId + ".jpg");

        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            localImagePath = imageFile.getAbsolutePath();
            Log.d("AdminAddNewTrainerActivity", "Сохраненный путь к изображению: " + localImagePath);
            Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show();

            saveTrainerInfoToDatabase();

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка сохранения изображения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
    }

    private void saveTrainerInfoToDatabase() {
        HashMap<String, Object> trainerMap = new HashMap<>();
        trainerMap.put("trainerId", trainerId);
        trainerMap.put("name", trainerName);
        trainerMap.put("specialty", specialty);
        trainerMap.put("imageUrl", localImagePath);

        DatabaseReference trainersRef = FirebaseDatabase.getInstance().getReference().child("Trainers");
        trainersRef.child(trainerId).setValue(trainerMap)
                .addOnCompleteListener(task -> {
                    loadingBar.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminAddNewTrainerActivity.this, "Тренер добавлен", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminAddNewTrainerActivity.this, AdminCategoryActivity.class));
                    } else {
                        String message = task.getException() != null ? task.getException().toString() : "Неизвестная ошибка";
                        Toast.makeText(AdminAddNewTrainerActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                trainerImage.setImageURI(imageUri);
            } else {
                Toast.makeText(this, "Ошибка: изображение не выбрано.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        trainerImage = findViewById(R.id.select_trainer_image);
        trainerNameInput = findViewById(R.id.trainer_name);
        specialtyInput = findViewById(R.id.trainer_specialty);
        addTrainerButton = findViewById(R.id.btn_add_new_trainer);
        loadingBar = new ProgressDialog(this);
    }
}