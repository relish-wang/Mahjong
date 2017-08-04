package wang.relish.netspeed.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import wang.relish.netspeed.NetSpeed;
import wang.relish.netspeed.OnSpeedUpdatedAdapter;
import wang.relish.netspeed.OnSpeedUpdatedListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvspeed1 = (TextView) findViewById(R.id.tv_speed1);
        final TextView tvspeed2 = (TextView) findViewById(R.id.tv_speed2);

        NetSpeed.addSpeedUpdateListener(new OnSpeedUpdatedListener() {
            @Override
            public void onSpeedUpdated(long speed) {
                tvspeed1.setText(speed + "B/s");
            }
        });

        NetSpeed.addSpeedUpdateListener(new OnSpeedUpdatedAdapter(4) {
            @Override
            public void onSpeedUpdatedPerInterval(long speed) {
                tvspeed2.setText(speed + "B/s");
            }
        });


        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetSpeed.startListen();
            }
        });

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection conn = (HttpURLConnection) new URL("https://www.baidu.com/").openConnection();
                            conn.setConnectTimeout(5000);
                            conn.setRequestMethod("GET");

                            final int code = conn.getResponseCode();
                            InputStream is = conn.getInputStream(); // 字节流转换成字符串
                            final String result = streamToString(is);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "code = " + code, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "网络连接失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetSpeed.stopListen();
        Toast.makeText(this, "stop listening", Toast.LENGTH_SHORT).show();
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     */

    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e("tag", e.toString());
            return null;
        }
    }
}