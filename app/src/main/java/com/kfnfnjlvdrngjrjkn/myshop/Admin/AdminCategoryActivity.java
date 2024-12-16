package com.kfnfnjlvdrngjrjkn.myshop.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kfnfnjlvdrngjrjkn.myshop.R;
import com.kfnfnjlvdrngjrjkn.myshop.Users.HomeActivity;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView equipment, sportpit, trainer;
    private Button button_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        init();
        equipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentE = new Intent(AdminCategoryActivity.this, com.kfnfnjlvdrngjrjkn.myshop.Admin.AdminAddNewProductActivity.class);
                intentE.putExtra("category", "Экиперовка");
                startActivity(intentE);
            }
        });
        sportpit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentE = new Intent(AdminCategoryActivity.this, com.kfnfnjlvdrngjrjkn.myshop.Admin.AdminAddNewProductActivity.class);
                intentE.putExtra("category", "Спортивное питание");
                startActivity(intentE);
            }
        });
        trainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentE = new Intent(AdminCategoryActivity.this, com.kfnfnjlvdrngjrjkn.myshop.Admin.AdminAddNewTrainerActivity.class);

                startActivity(intentE);
            }
        });

        button_home.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentE = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                startActivity(intentE);
            }
        }));
    }
    private void init(){
        equipment = findViewById(R.id.equipment);
        sportpit = findViewById(R.id.sportpit);
        button_home = findViewById(R.id.button_home);
        trainer = findViewById(R.id.trainer);
    }
}