package wang.relish.netspeed;

/**
 * 网速更新监视器
 *
 * @author wangxin
 * @since 2017/08/02
 */
public interface OnSpeedUpdatedListener {

    /**
     * 更新网速
     *
     * @param speed 网速（单位:Byte）
     */
    void onSpeedUpdated(long speed);
}