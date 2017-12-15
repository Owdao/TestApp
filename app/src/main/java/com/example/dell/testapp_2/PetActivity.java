package com.example.dell.testapp_2;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PetActivity extends AppCompatActivity {

    private TextView text1;
    private Button button1;
    private SurfaceView surfaceView1;

    private SilkyAnimation silkyAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        text1=this.findViewById(R.id.text_pet_2);
        text1.setText("LEVEL："+((MyApplication)getApplicationContext()).getLevel());
        button1=this.findViewById(R.id.button_pet_1);
        surfaceView1=this.findViewById(R.id.surfaceView_pet);

        silkyAnimation = new SilkyAnimation.Builder(surfaceView1)
                .setCacheCount(8)
                .setFrameInterval(40)
                .setScaleType(SilkyAnimation.SCALE_TYPE_CENTER)
                .setRepeatMode(SilkyAnimation.MODE_INFINITE)
                .build();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                silkyAnimation.start("nocommand");
            }
        },40);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
