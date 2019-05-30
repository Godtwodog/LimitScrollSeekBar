package com.god2dog.limitscrollseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private LimitScrollSeekBar limitScrollSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        limitScrollSeekBar = findViewById(R.id.limit_scroll_seek_bar);

        findViewById(R.id.btn_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limitScrollSeekBar.minusProgress();
            }
        });

        findViewById(R.id.btn_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limitScrollSeekBar.plusProgress();
            }
        });

        limitScrollSeekBar.setListener(new LimitScrollSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(LimitScrollSeekBar seekBar, int progress, float progressPercent, boolean fromUser) {
                Toast.makeText(MainActivity.this, progressPercent+":"+progress, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
