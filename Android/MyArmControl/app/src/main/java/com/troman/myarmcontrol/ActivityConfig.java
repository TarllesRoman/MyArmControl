package com.troman.myarmcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.PathMotion;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityConfig extends AppCompatActivity {

    private SeekBar skMotor1, skMotor2, skMotor3, skMotor4;
    private EditText etMacBt;
    private TextView tvMotor1, tvMotor2, tvMotor3, tvMotor4;

    private SharedPreferences settings;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        etMacBt = findViewById(R.id.etMacBt);

        etMacBt.setText(MainActivity.MAC_BT);

        skMotor1 = findViewById(R.id.skMotor1);
        tvMotor1 = findViewById(R.id.tvMotor1);
        tvMotor1.setText(String.format("%s : %d", getString(R.string.motor_1), MainActivity.PMOTOR_1));

        skMotor2 = findViewById(R.id.skMotor2);
        tvMotor2 = findViewById(R.id.tvMotor2);
        tvMotor2.setText(String.format("%s : %d", getString(R.string.motor_2), MainActivity.PMOTOR_2));

        skMotor3 = findViewById(R.id.skMotor3);
        tvMotor3 = findViewById(R.id.tvMotor3);
        tvMotor3.setText(String.format("%s : %d", getString(R.string.motor_3), MainActivity.PMOTOR_3));

        skMotor4 = findViewById(R.id.skMotor4);
        tvMotor4 = findViewById(R.id.tvMotor4);
        tvMotor4.setText(String.format("%s : %d", getString(R.string.motor_4), MainActivity.PMOTOR_4));


        skMotor1.setProgress(MainActivity.PMOTOR_1);
        skMotor1.setOnSeekBarChangeListener(onSeekBarChangeListener);

        skMotor2.setProgress(MainActivity.PMOTOR_2);
        skMotor2.setOnSeekBarChangeListener(onSeekBarChangeListener);

        skMotor3.setProgress(MainActivity.PMOTOR_3);
        skMotor3.setOnSeekBarChangeListener(onSeekBarChangeListener);

        skMotor4.setProgress(MainActivity.PMOTOR_4);
        skMotor4.setOnSeekBarChangeListener(onSeekBarChangeListener);

    }

    public void onclickSave(View view){
        MainActivity.MAC_BT = etMacBt.getText().toString();

        settings = getSharedPreferences("MAControl", MODE_PRIVATE);
        edit = settings.edit();

        edit.putString("MAC_BT", MainActivity.MAC_BT);
        edit.commit();

        Toast.makeText(this,"MAC bluetooth salvo!",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()){
                case R.id.skMotor1:
                    tvMotor1.setText(String.format("%s : %d", getString(R.string.motor_1), progress));
                    break;
                case R.id.skMotor2:
                    tvMotor2.setText(String.format("%s : %d", getString(R.string.motor_2), progress));
                    break;
                case R.id.skMotor3:
                    tvMotor3.setText(String.format("%s : %d", getString(R.string.motor_3), progress));
                    break;
                case R.id.skMotor4:
                    tvMotor4.setText(String.format("%s : %d", getString(R.string.motor_4), progress));
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();

            switch (seekBar.getId()){
                case R.id.skMotor1:
                    MainActivity.PMOTOR_1 = seekBar.getProgress();
                    tvMotor1.setText(String.format("%s : %d", getString(R.string.motor_1), MainActivity.PMOTOR_1));
                    break;
                case R.id.skMotor2:
                    MainActivity.PMOTOR_2 = seekBar.getProgress();
                    tvMotor2.setText(String.format("%s : %d", getString(R.string.motor_2), MainActivity.PMOTOR_2));
                    break;
                case R.id.skMotor3:
                    MainActivity.PMOTOR_3 = seekBar.getProgress();
                    tvMotor3.setText(String.format("%s : %d", getString(R.string.motor_3), MainActivity.PMOTOR_3));
                    break;
                case R.id.skMotor4:
                    MainActivity.PMOTOR_4 = seekBar.getProgress();
                    tvMotor4.setText(String.format("%s : %d", getString(R.string.motor_4), MainActivity.PMOTOR_4));
                    break;
            }
        }
    };
}
