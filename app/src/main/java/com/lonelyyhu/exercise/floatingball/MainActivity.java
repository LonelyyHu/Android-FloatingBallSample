package com.lonelyyhu.exercise.floatingball;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickFloatBall1(View view) {

        Intent intent = new Intent(this, FloatBall1Activity.class);
        startActivity(intent);

    }

    public void onClickFloatBall2(View view) {

        Intent intent = new Intent(this, FloatBall2Activity.class);
        startActivity(intent);

    }
}
