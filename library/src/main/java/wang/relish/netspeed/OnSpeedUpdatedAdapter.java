package wang.relish.netspeed;

/**
 * 更新当前网速
 *
 * @author wangxin
 * @since 2017/08/02
 */
public abstract class OnSpeedUpdatedAdapter implements OnSpeedUpdatedListener {

    /**
     * 更新网速的时间间隔（单位：秒）
     */
    private int mUpdateInterval;

    private int count = 0;

    private long sum = 0;

    /**
     * 网速监视器
     *
     * @param updateInterval 更新网速的时间间隔（单位：秒）
     */
    public OnSpeedUpdatedAdapter(int updateInterval) {
        mUpdateInterval = updateInterval;
    }

    @Override
    public void onSpeedUpdated(long speed) {
        sum += speed;
        count++;
        if (count == mUpdateInterval) {
            onSpeedUpdatedPerInterval(sum / mUpdateInterval);
            count = 0;
            sum = 0;
        }
    }

    public abstract void onSpeedUpdatedPerInterval(long speed);
}