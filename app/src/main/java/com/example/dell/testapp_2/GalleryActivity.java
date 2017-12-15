package com.example.dell.testapp_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GalleryActivity extends AppCompatActivity {

    private TextView text_time;
    private TextView text_count;
    private TextView text_length;
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        text_count=this.findViewById(R.id.text_gallery_4);
        text_count.setText(String.valueOf(((MyApplication)getApplicationContext()).getRidingCount())
                            +"  次");
        text_length=this.findViewById(R.id.text_gallery_6);
        text_length.setText(String.valueOf(((MyApplication)getApplicationContext()).getRidingLength())
                            +"  公里");
        text_time=this.findViewById(R.id.text_gallery_2);
        text_time.setText(getStringTime(((MyApplication)getApplicationContext()).getRidingTime()));

        button1=this.findViewById(R.id.button_gallery_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private String getStringTime(int count){
        int hour,min,sec;
        hour=(int)count/3600;
        min=(int)count%3600/60;
        sec=(int)count%60;
        return String.format("%02d:%02d:%02d",hour,min,sec);
    }
}
