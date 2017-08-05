# NetSpeed

作者：[Relish Wang](https://github.com/relish-wang)

[TOC]
## 简介

NetSpeed是一个网速测量工具。

### 更新历史

[**CHANGELOG.md**](CHANGELOG.md)

## 使用方法

### 1 初始化（Application中）
```
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetSpeed.init(this);
    }
}
```

### 2 设置网速监听器

#### 2.1 默认网速监听器（OnSpeedUpdatedListener）
```
/**
 * 默认的监听器每2s更新一次网速(即显示的网速表示过去2秒内的平均网速)
 * 单位：Byte/s
 */
NetSpeed.addSpeedUpdateListener(new OnSpeedUpdatedListener() {
   @Override
   public void onSpeedUpdated(long speed) {
       tv_show_speed.setText(speed + "B/s");
   }
});
```

#### 2.2 网速监听适配器（OnSpeedUpdatedAdapter）
```
/**
 * updateInterval表示，每updateInterval秒更新一次网速（即显示的网速表示过去updateInterval秒内的平均网速）
 * 单位：Byte/s
 */
NetSpeed.addSpeedUpdateListener(new OnSpeedUpdatedAdapter(updateInterval) {
    @Override
    public void onSpeedUpdatedPerInterval(long speed) {
        tvspeed2.setText(speed + "B/s");
    }
});
```


### 3 停止监听
```
@Override
protected void onDestroy() {
    super.onDestroy();
    NetSpeed.stopListen();
}
```

## 注意事项

- 要求API Level 14以上

## 混淆配置

无

## 致谢

- 感谢[周骞](http://github.com/ylfzq)提供技术思路和指导(๑•ᴗ•๑)