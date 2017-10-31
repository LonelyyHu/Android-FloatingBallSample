package com.lonelyyhu.exercise.floatingball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FloatBall1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_ball1);

        showFloatView();
    }

    private void showFloatView(){
        FloatViewSample1 floatView = new FloatViewSample1.Builder(this, (ViewGroup) findViewById(R.id.root_view_float_activity_1))
                .setBottomMargin(0)
                .setRightMargin(0)
                .setHeight(225)
                .setWidth(225)
                .setDuration(500)
                .setRes(R.drawable.android)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FloatBall1Activity.this, "On Click", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.wtf("FloatBall1Activity", "onResume =>"+ this.getWindow().getDecorView().getBottom());

    }
}
