package com.kfnfnjlvdrngjrjkn.myshop.Users;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kfnfnjlvdrngjrjkn.myshop.Adapter.ProductAdapter;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Product;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;
import com.kfnfnjlvdrngjrjkn.myshop.Prevalent.Prevalent;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.kfnfnjlvdrngjrjkn.myshop.UI.ExitActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "onCreate called");
        setContentView(R.layout.activity_home);

        // Запрос разрешений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Меню");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, false);
        recyclerView.setAdapter(productAdapter);

        loadProducts(); // Загрузка продуктов
        loadUserData(); // Загрузка данных пользователя

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear(); // Очистка списка перед загрузкой новых данных
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class); // Получение объекта продукта
                    if (product != null) {
                        productList.add(product); // Добавление продукта в список
                    }
                }
                productAdapter.notifyDataSetChanged(); // Уведомление адаптера об изменении данных
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Ошибка загрузки продуктов", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        Users currentUser = Prevalent.getCurrentOnlineUser();
        if (currentUser != null) {
            String userPhone = currentUser.getPhone();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userPhone);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String imagePath = snapshot.child("image").getValue(String.class); // Путь к изображению

                        // Обновление UI
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        View headerView = navigationView.getHeaderView(0);
                        TextView userProfileName = headerView.findViewById(R.id.user_profile_name);
                        CircleImageView userProfileImage = headerView.findViewById(R.id.user_profile_image);

                        // Установка имени пользователя
                        if (name != null) {
                            userProfileName.setText(name);
                        }

                        // Загрузка изображения пользователя или установка изображения по умолчанию
                        if (imagePath != null && !imagePath.isEmpty()) {
                            Picasso.get().load("file://" + imagePath).into(userProfileImage); // Загрузка из локального хранилища
                        } else {
                            userProfileImage.setImageResource(R.drawable.user_profile_image);
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeActivity.this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Пользователь не найден. Пожалуйста, войдите в систему.", Toast.LENGTH_SHORT).show();

        }
    }
    // Логика добавления товара в корзину
    public void addToCart(Product product) {
        Users currentUser = Prevalent.getCurrentOnlineUser();
        if (currentUser != null) {
            currentUser.addToCart(product.getPid());
            updateCartInDatabase(currentUser);
            Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
        }
    }
// Передаем добавленнные товары в бд к юзеру
    private void updateCartInDatabase(Users user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getPhone());
        userRef.child("cartItems").setValue(user.getCartItems()).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {

                Toast.makeText(this, "Ошибка обновления корзины", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    // Переходы из бокового меню

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cart) {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_training) {
            Intent intent = new Intent(HomeActivity.this, TrainingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}