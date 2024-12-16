package com.kfnfnjlvdrngjrjkn.myshop.Admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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

public class AdminAddNewProductActivity extends AppCompatActivity {

    private static final int GALLERYPICK = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private String categoryName, Description, name, saveCurrentDate, saveCurrentTime, productRandomKey;
    private String localImagePath;
    private ImageView productImage;
    private EditText productName, productDescription, productPrice;
    private Button addNewProductButton;
    private Uri ImageUri;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        init();

        // Проверка разрешений
        if (checkStoragePermission()) {
            // Разрешение уже предоставлено
        } else {
            // Запрашиваем разрешение
            requestStoragePermission();
        }

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
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

    private void ValidateProductData() {
        Description = productDescription.getText().toString();
        name = productName.getText().toString();

        // Проверка цены и преобразование в double
        String Price = productPrice.getText().toString();
        double priceValue;

        if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Добавьте стоимость товара.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            try {
                priceValue = Double.parseDouble(Price); // Преобразование строки в double
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Неверный формат цены.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (ImageUri == null) {
            Toast.makeText(this, "Добавьте изображение товара.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Добавьте описание товара.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Добавьте название товара.", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation(priceValue); // Передаем значение цены в метод
        }
    }

    private void StoreProductInformation(double priceValue) {
        loadingBar.setTitle("Сохранение данных");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());
        productRandomKey = saveCurrentDate + saveCurrentTime;



        saveImageToFile(priceValue); // Передаем значение цены
    }

    private void saveImageToFile(double priceValue) {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!directory.exists()) {
            directory.mkdirs(); // Создание директории, если она не существует
        }

        File imageFile = new File(directory, productRandomKey + ".jpg");

        try (InputStream inputStream = getContentResolver().openInputStream(ImageUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            localImagePath = imageFile.getAbsolutePath(); // Сохранение пути к файлу
            Log.d("AdminAddNewProductActivity", "Сохраненный путь к изображению: " + localImagePath);
            Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show();

            SaveProductInfoToDatabase(priceValue); // Передаем значение цены

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка сохранения изображения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            loadingBar.dismiss(); // Закрываем диалог загрузки в случае ошибки
        }
    }

    private void SaveProductInfoToDatabase(double priceValue) {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", localImagePath); // Использование локального пути
        productMap.put("category", categoryName);
        productMap.put("price", priceValue); // Использование double вместо String
        productMap.put("name", name);

        DatabaseReference ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ProductsRef.child(productRandomKey).setValue(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAddNewProductActivity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(loginIntent);
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERYPICK && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            if (ImageUri != null) {
                productImage.setImageURI(ImageUri);
            } else {
                Toast.makeText(this, "Ошибка: изображение не выбрано.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        categoryName = getIntent().getExtras().get("category").toString();
        productImage = findViewById(R.id.select_product_image);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        addNewProductButton = findViewById(R.id.btn_add_new_product);
        loadingBar = new ProgressDialog(this);
    }
}