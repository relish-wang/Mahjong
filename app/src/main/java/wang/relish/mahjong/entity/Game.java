package wang.relish.mahjong.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static wang.relish.mahjong.entity.Game.MAHJONG;
import static wang.relish.mahjong.entity.Game.RED_TEN;
import static wang.relish.mahjong.entity.Game.UNDEFINED;

/**
 * @author Relish Wang
 * @since 2018/02/20
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({UNDEFINED, MAHJONG, RED_TEN})
public @interface Game {
    int UNDEFINED = -1;
    int MAHJONG = 0;
    int RED_TEN = 1;
}

