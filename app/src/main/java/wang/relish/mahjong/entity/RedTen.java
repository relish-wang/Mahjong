package wang.relish.mahjong.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static wang.relish.mahjong.entity.RedTen.DOUBLE_SHUT;
import static wang.relish.mahjong.entity.RedTen.DOGFALL;
import static wang.relish.mahjong.entity.RedTen.SINGLE_SHUT;
import static wang.relish.mahjong.entity.RedTen.TRIPLE_SHUT;

/**
 * @author Relish Wang
 * @since 2018/02/20
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({DOGFALL, SINGLE_SHUT, DOUBLE_SHUT, TRIPLE_SHUT})
public @interface RedTen {
    int DOGFALL = 0; // 平局
    int SINGLE_SHUT = 1; // 单关
    int DOUBLE_SHUT = 2; // 双关
    int TRIPLE_SHUT = 3; // 三关
}
