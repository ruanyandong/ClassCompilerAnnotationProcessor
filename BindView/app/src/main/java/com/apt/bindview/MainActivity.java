package com.apt.bindview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import com.apt.annotation.SensorsDataBindView;
import com.apt.sdk.SensorsDataAPI;

public class MainActivity extends AppCompatActivity {

    @SensorsDataBindView(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorsDataAPI.bindView(this);
        textView.setText("绑定成功");
    }
}