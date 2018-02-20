package wang.relish.mahjong.callback;

import wang.relish.mahjong.entity.User;

/**
 * @author Relish Wang
 * @since 2018/02/15
 */
public interface UserCallback {
    void onUserGet(User...user);
}
