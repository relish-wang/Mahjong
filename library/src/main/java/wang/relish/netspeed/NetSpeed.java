package wang.relish.netspeed;

import android.content.Context;

/**
 * 网络流量统计SDK
 *
 * @author 王鑫
 * @since 2017/05/11
 */
public class NetSpeed {

    private static int sUid;
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
        sUid = context.getApplicationInfo().uid;
    }

    /**
     * 添加当前应用的网速监听器
     *
     * @param listener 网速监听器
     */
    public static void addSpeedUpdateListener(OnSpeedUpdatedListener listener) {
        NetSpeedMeasurement.getInstance().addSpeedUpdatedListener(
                sUid, listener);
    }

    /**
     * 开始网速监听
     */
    public static void startListen() {
        NetSpeedMeasurement.getInstance().startListen();
    }

    /**
     * 移除当前应用所有监听器
     */
    public static void removeAllListener() {
        NetSpeedMeasurement.getInstance().removeAllListener(sUid);
    }

    /**
     * 移除当前应用所有监听器
     */
    public static void removeListener(OnSpeedUpdatedListener l) {
        NetSpeedMeasurement.getInstance().removeSpeedUpdatedListener(l);
    }

    /**
     * 立即停止监听（并移除所有监听器）
     */
    public static void stopListen() {
        NetSpeedMeasurement.getInstance().stopListen();
    }

    public static Context getContext() {
        return sContext;
    }
}
