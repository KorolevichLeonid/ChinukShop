package com.kfnfnjlvdrngjrjkn.myshop.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kfnfnjlvdrngjrjkn.myshop.Model.Product;
import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.kfnfnjlvdrngjrjkn.myshop.Users.CartActivity;
import com.kfnfnjlvdrngjrjkn.myshop.Users.HomeActivity;

import java.io.File;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;//для доступа к ресурсам и вызова методов активности
    private List<Product> productList;
    private boolean isCart; // Флаг для определения, отображается ли корзина

    public ProductAdapter(Context context, List<Product> productList, boolean isCart) {
        this.context = context;
        this.productList = productList;
        this.isCart = isCart;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override//для заполнения данных в ViewHolder на основе позиции
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));

        // Загружаем изображение из локального хранилища
        String imagePath = product.getImage();
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.productImage.setImageBitmap(bitmap);
        } else {
            holder.productImage.setImageResource(R.drawable.chinuk_2); // Ошибка
        }

        // Логика на добавление и удалениие из корзины
        if (isCart) {
            holder.buttonAddToCart.setText("Удалить из корзины");
            holder.buttonAddToCart.setOnClickListener(v -> {

                ((CartActivity) context).removeFromCart(product);
            });
        } else {
            holder.buttonAddToCart.setText("Добавить в корзину");
            holder.buttonAddToCart.setOnClickListener(v -> {
                ((HomeActivity) context).addToCart(product);
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView productImage;
        TextView productDescription;
        TextView productPrice;
        Button buttonAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            buttonAddToCart = itemView.findViewById(R.id.button_add_to_cart);
        }
    }
}