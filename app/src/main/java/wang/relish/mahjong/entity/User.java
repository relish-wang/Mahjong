package wang.relish.mahjong.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wang.relish.mahjong.db.DBHelper;
import wang.relish.mahjong.util.ToastUtil;

/**
 * @author Relish Wang
 * @since 2018/02/15
 */
public class User implements Serializable {

    private long id;
    private String name;
    private long score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    /**
     * 获取用户的最大ID
     */
    public static long getMaxId() {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select max(id) from user", null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                return cursor.getLong(0);
            } else {
                return 0;
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public static boolean isExist(String name) {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from user where name = ?", new String[]{name});
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public static boolean createUser(String name) {
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show("名字不得为空");
            return false;
        }
        if (isExist(name)) {
            ToastUtil.show("用户已存在");
            return false;
        }
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        long maxId = getMaxId() + 1;
        db.execSQL("insert into user(id,name,score) values(?,?,?)", new String[]{maxId + "", name, "0"});
        return isExist(name);
    }

    public static List<User> getUsers() {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select id,name,score from user", null);
            if (cursor != null && cursor.getCount() > 0) {
                List<User> users = new ArrayList<>();
                User user;
                while (cursor.moveToNext()) {
                    user = new User();
                    long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    long score = cursor.getLong(2);
                    user.setId(id);
                    user.setName(name);
                    user.setScore(score);
                    users.add(user);
                }
                return users;
            }
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public static boolean deleteUser(long id) {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.execSQL("delete from user where id = ?", new String[]{id + ""});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean reset() {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("score", 0);
        try {
            db.update("user", cv, null, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static long getScoreById(long id) {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select score from user where id = ?", new String[]{id + ""});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            } else {
                return -2;
            }
        } catch (Exception e) {
            return -1;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public static String getNameById(long id) {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select name from user where id = ?", new String[]{id + ""});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
