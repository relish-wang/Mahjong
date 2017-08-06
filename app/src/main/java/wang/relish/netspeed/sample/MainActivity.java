package wang.relish.netspeed.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import wang.relish.netspeed.NetSpeed;
import wang.relish.netspeed.OnSpeedUpdatedAdapter;
import wang.relish.netspeed.OnSpeedUpdatedListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_listen, tv_adapter;

    private Button btn_start, btn_net_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_listen = (TextView) findViewById(R.id.tv_listen);
        tv_adapter = (TextView) findViewById(R.id.tv_adapter);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_net_request = (Button) findViewById(R.id.btn_net_request);

        btn_start.setOnClickListener(this);
        btn_net_request.setOnClickListener(this);

        //默认监听器：每2s更新一次网速（即过去2s内的平均网速）
        NetSpeed.addSpeedUpdateListener(new OnSpeedUpdatedListener() {
            @Override
            public void onSpeedUpdated(long speed) {
                tv_listen.setText(speed + "B/s");
            }
        });

        //网速适配器：每4s(依构造方法的入参决定,传几就是几s)更新一次网速（即过去4s内的平均网速）
        NetSpeed.addSpeedUpdateListener(new OnSpeedUpdatedAdapter(4) {
            @Override
            public void onSpeedUpdatedPerInterval(long speed) {
                tv_adapter.setText(speed + "B/s");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                //开始网速监听
                NetSpeed.startListen();
                break;
            case R.id.btn_net_request:
                //网络请求（产生流量消耗，可以直观地显示网速变化）
                Thread thread = new Thread(new NetRequestRunnable(MainActivity.this));
                thread.start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetSpeed.stopListen();//停止网速监听
        Toast.makeText(this, "stop listening", Toast.LENGTH_SHORT).show();
    }
}