package wang.relish.netspeed.sample;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Relish Wang
 * @since 2017/08/05
 */
public class NetRequestRunnable implements Runnable {

    private Activity activity;

    public NetRequestRunnable(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://www.baidu.com/").openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            final int code = conn.getResponseCode();
            InputStream is = conn.getInputStream(); // 字节流转换成字符串
            final String result = streamToString(is);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "code = " + code, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "网络连接失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     */
    private static String streamToString(InputStream is) {
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
