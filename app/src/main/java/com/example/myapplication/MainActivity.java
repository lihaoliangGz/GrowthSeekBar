package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GrowthSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setCurrentValue("2000");
        seekBar.setDescentName("宝迷");
        seekBar.setDescentValue("2000");

        seekBar.setAscentName("老铁");
        seekBar.setAsscentValue("20000");
        seekBar.postInvalidate();
    }
}
