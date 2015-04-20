package lab.s2jh.core.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class DateUtils {

    public final static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public final static String SHORT_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public final static String[] MULTI_FORMAT = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM" };

    public final static DateFormat DEFAULT_TIME_FORMATER = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

    public final static DateFormat DEFAULT_DATE_FORMATER = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    public final static DateFormat SHORT_TIME_FORMATER = new SimpleDateFormat(SHORT_TIME_FORMAT);

    public final static String FORMAT_YYYY = "yyyy";

    public final static String FORMAT_YYYYMM = "yyyyMM";

    public final static String FORMAT_YYYYMMDD = "yyyyMMdd";

    public final static DateFormat FORMAT_YYYY_FORMATER = new SimpleDateFormat(FORMAT_YYYY);

    public final static DateFormat FORMAT_YYYYMM_FORMATER = new SimpleDateFormat(FORMAT_YYYYMM);

    public final static DateFormat FORMAT_YYYYMMDD_FORMATER = new SimpleDateFormat(FORMAT_YYYYMMDD);

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return DEFAULT_DATE_FORMATER.format(date);
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) {
            return null;
        }
        return DEFAULT_TIME_FORMATER.format(date);
    }

    public static String formatShortTime(Date date) {
        if (date == null) {
            return null;
        }
        return SHORT_TIME_FORMATER.format(date);
    }

    public static String formatDateNow() {
        return formatDate(new Date());
    }

    public static String formatTimeNow() {
        return formatTime(new Date());
    }

    public static Date parseDate(String date, DateFormat df) {
        if (date == null) {
            return null;
        }
        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseTime(String date, DateFormat df) {
        if (date == null) {
            return null;
        }
        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseDate(String date) {
        return parseDate(date, DEFAULT_DATE_FORMATER);
    }

    public static Date parseTime(String date) {
        return parseTime(date, DEFAULT_TIME_FORMATER);
    }

    public static String plusOneDay(String date) {
        DateTime dateTime = new DateTime(parseDate(date).getTime());
        return formatDate(dateTime.plusDays(1).toDate());
    }

    public static String plusOneDay(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        return formatDate(dateTime.plusDays(1).toDate());
    }

    public static String getHumanDisplayForTimediff(Long diffMillis) {
        if (diffMillis == null) {
            return "";
        }
        long day = diffMillis / (24 * 60 * 60 * 1000);
        long hour = (diffMillis / (60 * 60 * 1000) - day * 24);
        long min = ((diffMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long se = (diffMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day + "D");
        }
        DecimalFormat df = new DecimalFormat("00");
        sb.append(df.format(hour) + ":");
        sb.append(df.format(min) + ":");
        sb.append(df.format(se));
        return sb.toString();
    }

    /**
     * 把类似2014-01-01 ~ 2014-01-30格式的单一字符串转换为两个元素数组
     */
    public static Date[] parseBetweenDates(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        date = date.replace("～", "~");
        Date[] dates = new Date[2];
        String[] values = date.split("~");
        dates[0] = parseMultiFormatDate(values[0].trim());
        dates[1] = parseMultiFormatDate(values[1].trim());
        return dates;
    }

    public static Date parseMultiFormatDate(String date) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(date, MULTI_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
