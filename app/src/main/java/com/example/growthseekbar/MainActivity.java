package com.example.growthseekbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.cvbmgh.growthseekbar.GrowthSeekBar;

public class MainActivity extends AppCompatActivity {

    private GrowthSeekBar growthSeekBar;
    private Switch switchView;
    private SeekBar seekBar;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        growthSeekBar = findViewById(R.id.growthSeekBar);

        growthSeekBar.setCurrentValue("10000");
        growthSeekBar.setDescentName("宝迷");
        growthSeekBar.setDescentValue("1500");

        growthSeekBar.setAscentName("老铁");
        growthSeekBar.setAsscentValue("20000");
        growthSeekBar.postInvalidate();

        switchView = findViewById(R.id.switchView);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    growthSeekBar.setInterval(4);
                } else {
                    growthSeekBar.setInterval(3);
                }
                growthSeekBar.postInvalidate();
                growthSeekBar.requestLayout();
            }
        });

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                growthSeekBar.setCurrentValue(String.valueOf(progress));
                growthSeekBar.postInvalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    growthSeekBar.setCurrentValue(s.toString());
                    growthSeekBar.postInvalidate();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
