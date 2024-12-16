package com.kfnfnjlvdrngjrjkn.myshop.Adapter;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kfnfnjlvdrngjrjkn.myshop.Model.Product;
import com.kfnfnjlvdrngjrjkn.myshop.Model.Users;
import com.kfnfnjlvdrngjrjkn.myshop.Prevalent.Prevalent;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartAdapter extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        Button orderButton = findViewById(R.id.order);

        // Загружаем товары в корзину
        loadCartItems();

        // Настраиваем адаптер и RecyclerView
        productAdapter = new ProductAdapter(this, cartItems, true); // true для корзины
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(productAdapter);

        // Обработчик нажатия на кнопку оформления заказа
        orderButton.setOnClickListener(v -> checkout());
    }

    private void loadCartItems() {
        Users currentUser = Prevalent.getCurrentOnlineUser();
        if (currentUser != null) {
            for (String productId : currentUser.getCartItems()) {
                // Загружаем информацию о продукте по ID из бд
                DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products").child(productId);
                productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Product product = snapshot.getValue(Product.class);
                            if (product != null) {
                                cartItems.add(product); // Добавляем продукт в список корзины
                                productAdapter.notifyDataSetChanged(); // Уведомляем адаптер об изменениях
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CartAdapter.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void checkout() {
        Users currentUser = Prevalent.getCurrentOnlineUser();
        if (currentUser != null) {
            List<String> cartItemsIds = currentUser.getCartItems();

            // не пуста ли корзина
            if (cartItemsIds.isEmpty()) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаем новый заказ в бд
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(currentUser.getPhone());

            //  для хранения информации о заказе
            HashMap<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("userPhone", currentUser.getPhone());
            orderDetails.put("orderItems", cartItemsIds); // Сохраняем список ID товаров
            orderDetails.put("orderStatus", "Ожидает выполнения"); // Статус заказа

            // Записываем инфу
            ordersRef.push().setValue(orderDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CartAdapter.this, "Заказ оформлен", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CartAdapter.this, "Ошибка оформления заказа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Пожалуйста, войдите в систему", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromCart(Product product) {
        Users currentUser = Prevalent.getCurrentOnlineUser();
        if (currentUser != null) {
            currentUser.removeFromCart(product.getPid());
            updateCartInDatabase(currentUser);
            cartItems.remove(product);
            productAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Товар удален из корзины", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCartInDatabase(Users user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getPhone());
        userRef.child("cartItems").setValue(user.getCartItems()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Корзина обновлена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ошибка обновления корзины", Toast.LENGTH_SHORT).show();
            }
        });
    }
}