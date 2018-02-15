package wang.relish.mahjong.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wang.relish.mahjong.db.DBHelper;

/**
 * @author Relish Wang
 * @since 2018/02/15
 */
public class Record implements Serializable, Comparable<Record> {

    private long id;
    private long createTime;
    private long winnerId;
    private long loserId;// 为0表示winer自摸,非0表示放铳

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(long winnerId) {
        this.winnerId = winnerId;
    }

    public long getLoserId() {
        return loserId;
    }

    public void setLoserId(long loserId) {
        this.loserId = loserId;
    }

    public static long maxId() {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select max(id) from record", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean save() {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into record(id,createTime,winnerId,loserId) values(?,?,?,?)",
                new String[]{id + "", createTime + "", winnerId + "", loserId + ""});
        if (loserId == 0) {//自摸
            db.execSQL("update user set score = score - 3 where id!=?", new String[]{winnerId + ""});
            db.execSQL("update user set score = score + 9 where id=?", new String[]{winnerId + ""});
        } else { //放铳
            db.execSQL("update user set score = score - 1 where id!=? and id!=?", new String[]{winnerId + "", loserId + ""});
            db.execSQL("update user set score = score + 4 where id=?", new String[]{winnerId + ""});
            db.execSQL("update user set score = score - 2 where id=?", new String[]{loserId + ""});
        }
        return isExist(id);
    }

    public static boolean isExist(long id) {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from record where id = ?", new String[]{id + ""});
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public static List<Record> getRecords() {
        DBHelper helper = new DBHelper();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        List<Record> records = new ArrayList<>();
        try {
            cursor = db.rawQuery("select id, createTime, winnerId, loserId from record", null);
            if (cursor != null && cursor.moveToFirst()) {
                Record record = null;
                do {
                    long id = cursor.getLong(0);
                    long createTime = cursor.getLong(1);
                    long winnerId = cursor.getLong(2);
                    long loserId = cursor.getLong(3);
                    record = new Record();
                    record.setId(id);
                    record.setCreateTime(createTime);
                    record.setWinnerId(winnerId);
                    record.setLoserId(loserId);
                    records.add(record);
                } while (cursor.moveToNext());
                return records;
            }
            return records;
        } catch (Exception e) {
            return records;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override
    public int compareTo(@NonNull Record record) {
        return (int) (createTime - record.createTime);
    }
}
