package feeker.net.tools.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date工具类
 * Created by LGZ on 2016/7/20.
 */
public class DateUtil {
    public final static String FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat format = null;

    private DateUtil() {
    }

    private static SimpleDateFormat initFormat(String formatString) {
        if (format == null) format = new SimpleDateFormat(formatString);
        return format;
    }

    public static String format(Date date, String formatString) {
        format = initFormat(formatString);
        return format.format(date);
    }

    public static Date parse(String dateString, String formatString) {
        format = initFormat(formatString);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}
