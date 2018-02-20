package wang.relish.mahjong.callback;

import wang.relish.mahjong.entity.RedTen;

/**
 * @author Relish Wang
 * @since 2018/02/21
 */
public interface RedTenCallback {

    void onRedTenGet(@RedTen int redTen);
}
