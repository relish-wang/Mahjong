package wang.relish.netspeed.sample;

import android.app.Application;

import wang.relish.netspeed.NetSpeed;

/**
 * @author Relish Wang
 * @since 2017/08/04
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        NetSpeed.init(this);
    }
}
