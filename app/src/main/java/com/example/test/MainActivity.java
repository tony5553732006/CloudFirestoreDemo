package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.test.R;

public class MainActivity extends AppCompatActivity {
    private Button mCloudFireStoreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCloudFireStoreBtn = findViewById(R.id.cloudFireStoreBtn);
        mCloudFireStoreBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CloudFireStoreDemo.class);
            startActivity(intent);
        });
    }
}