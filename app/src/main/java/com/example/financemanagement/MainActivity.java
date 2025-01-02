package com.example.financemanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    static int loginStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        String accessToken = sharedPreferences.getString("access_token", null);
        if (refreshToken != null && accessToken != null) {
            loginStatus = 1;
        } else {
            loginStatus = 0;
        }
        Button btnStartApp = findViewById(R.id.btnStartApp);
        btnStartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (loginStatus==1) {
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                } else {

                    intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                }
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}