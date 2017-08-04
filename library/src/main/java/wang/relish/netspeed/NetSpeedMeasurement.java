package wang.relish.netspeed;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 实时网速计算（与统计无关）
 *
 * @author Relish Wang
 * @since 2017/07/25
 */
class NetSpeedMeasurement {

    private long lastTimeStamp = 0;

    private NetSpeedMeasurement() {
        mListenerMap = new SparseArray<>();
        mLastTotalRxBytes = new SparseArray<>();
    }

    private static volatile NetSpeedMeasurement instance;

    static synchronized NetSpeedMeasurement getInstance() {
        if (instance == null) {
            instance = new NetSpeedMeasurement();
        }
        return instance;
    }

    private static final int SPEED_UPDATE_TAG = 0x1001;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SPEED_UPDATE_TAG:
                    int updateInterval = (int) msg.obj;
                    if (updateInterval <= 0) return;

                    netSpeedOnListening();

                    Message message = Message.obtain();
                    message.what = SPEED_UPDATE_TAG;
                    message.obj = updateInterval;
                    sendMessageDelayed(message, updateInterval * 1000);
                    break;
            }
        }
    };


    void startListen() {
        if (handler.hasMessages(SPEED_UPDATE_TAG)) {
            Toast.makeText(NetSpeed.getContext(), "Starting listening twice is forbidden.", Toast.LENGTH_SHORT).show();
            return;
        }
        lastTimeStamp = SystemClock.elapsedRealtime();
        for (int i = 0; i < mListenerMap.size(); i++) {
            int uid = mListenerMap.keyAt(i);
            mLastTotalRxBytes.put(uid, getTotalRxBytes(uid));
        }
        Message message = Message.obtain();
        message.what = SPEED_UPDATE_TAG;
        message.obj = 2;
        handler.sendMessageDelayed(message, 1000);
    }

    private static long getTotalRxBytes(int uid) {
        long size = TrafficStats.getUidRxBytes(uid);
        return size == TrafficStats.UNSUPPORTED/*表示不支持数据流量*/ ? 0 : size;//转为B
    }

    private void netSpeedOnListening() {
        long nowTimeStamp = SystemClock.elapsedRealtime();

        for (int i = 0; i < mListenerMap.size(); i++) {
            int uid = mListenerMap.keyAt(i);
            long nowTotalRxBytes = getTotalRxBytes(uid);
            final long speed = (nowTotalRxBytes - mLastTotalRxBytes.valueAt(i)) * 1000 / (nowTimeStamp - lastTimeStamp);

            mLastTotalRxBytes.setValueAt(i, nowTotalRxBytes);
            lastTimeStamp = nowTimeStamp;

            List<OnSpeedUpdatedListener> listeners = mListenerMap.get(uid);
            if (listeners == null) return;
            Iterator<OnSpeedUpdatedListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {//非UI线程；
                final OnSpeedUpdatedListener listener = iterator.next();
                try {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //切换到UI线程
                            listener.onSpeedUpdated(speed);//耗时操作导致回调时间间隔不准
                        }
                    });
                } catch (Exception e) {
                    // 移除当前监听器
                    iterator.remove();
                }
            }
        }
        lastTimeStamp = nowTimeStamp;
    }

    void addSpeedUpdatedListener(int uid, OnSpeedUpdatedListener l) {
        List<OnSpeedUpdatedListener> listeners = mListenerMap.get(uid);
        if (listeners == null) {
            listeners = new ArrayList<>();
            listeners.add(l);
            mListenerMap.put(uid, listeners);
        } else {
            mListenerMap.get(uid).add(l);
        }
    }

    void removeSpeedUpdatedListener(OnSpeedUpdatedListener l) {
        for (int i = 0; i < mListenerMap.size(); i++) {
            List<OnSpeedUpdatedListener> listeners = mListenerMap.valueAt(i);
            Iterator<OnSpeedUpdatedListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == l) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    void removeAllListener(int uid) {
        mListenerMap.remove(uid);
    }

    void removeAllListener() {
        mListenerMap.clear();
    }

    void stopListen() {
        removeAllListener();

        handler.removeMessages(SPEED_UPDATE_TAG);//立即停止

        Message message = Message.obtain();
        message.what = SPEED_UPDATE_TAG;
        message.obj = 0;//终止监听
        handler.sendMessage(message);
    }

    private SparseArray<List<OnSpeedUpdatedListener>> mListenerMap;//uid-listener

    private SparseArray<Long> mLastTotalRxBytes;//uid-bytes


}  