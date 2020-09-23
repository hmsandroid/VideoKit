package com.videokitnative.huawei.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.videokitnative.huawei.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Play(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }
}