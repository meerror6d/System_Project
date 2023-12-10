package com.example.basic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView prop, ez;
    private static int Splash_timeout = 3000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        prop = findViewById(R.id.property);
        ez = findViewById(R.id.easy);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(splashIntent);
                finish();
            }
        }, Splash_timeout);

        Animation myanim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation1);
        prop.startAnimation(myanim1);

        Animation myanim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation2);
        ez.startAnimation(myanim2);

    }
}