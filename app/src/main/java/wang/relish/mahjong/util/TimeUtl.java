package wang.relish.mahjong.util;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Relish Wang
 * @since 2018/02/19
 */
public class TimeUtl {

    public static boolean isSameDay(long t1, long t2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(t1);
        int y1 = c1.get(Calendar.YEAR);
        int m1 = c1.get(Calendar.MONTH);
        int d1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(t2);
        int y2 = c2.get(Calendar.YEAR);
        int m2 = c2.get(Calendar.MONTH);
        int d2 = c2.get(Calendar.DAY_OF_MONTH);
        return y1 == y2 && m1 == m2 && d1 == d2;
    }

    public static String getHHmm(long t){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return format.format(new Date(t));
    }

    public static String getyyyyMMdd(long t) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        return format.format(new Date(t));
    }
}
