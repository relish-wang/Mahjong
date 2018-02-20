package wang.relish.mahjong.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import wang.relish.mahjong.R;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("玩什么");
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_start_mahjong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MahjongActivity.start(MainActivity.this);
            }
        });
        findViewById(R.id.btn_start_red_ten).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RedTenActivity.start(MainActivity.this);
            }
        });
    }
}
