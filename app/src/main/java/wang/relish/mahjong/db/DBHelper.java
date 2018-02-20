package wang.relish.mahjong.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import wang.relish.mahjong.App;

/**
 * @author Relish Wang
 * @since 2018/02/15
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * 1->2: 加入红十
     */
    private static final int VERSION = 2;

    private static final String DB_NAME = "mahjong";

    public DBHelper() {
        super(App.CONTEXT, DB_NAME, null, VERSION);
    }


    private static final String CREATE_USER = "create table user(" +
            "id integer primary key," +
            "name text," +
            "score long);";
    private static final String CREATE_RECORD = "create table record(" +
            "id integer primary key," +
            "createTime timestamp," +
            "winnerId long," +
            "loserId long," +
            "game integer," +
            "isReversed integer," +
            "isBasked integer," +
            "result integer);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS record");
        onCreate(db);
    }
}
