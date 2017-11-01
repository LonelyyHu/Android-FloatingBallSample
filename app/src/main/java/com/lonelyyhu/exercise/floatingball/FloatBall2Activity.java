package com.lonelyyhu.exercise.floatingball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FloatBall2Activity extends AppCompatActivity {

    FloatViewSample2 floatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_ball2);


        floatView = new FloatViewSample2.Builder(this, (ViewGroup) findViewById(R.id.root_view_float_activity_2))
                .setBottomMargin(90)
                .setRightMargin(90)
                .setHeight(200)
                .setWidth(200)
                .setDuration(500)
                .setRes(R.drawable.android)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.wtf("FloatBall2Activity", "onClick !!!!!!!!!");

                        Toast.makeText(FloatBall2Activity.this, "On Click", Toast.LENGTH_LONG).show();
                    }
                })
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();

        floatView.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        floatView.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        floatView.destory();
    }
}
