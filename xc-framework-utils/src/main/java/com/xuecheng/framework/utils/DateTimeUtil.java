package com.xuecheng.framework.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class DateTimeUtil {

    /**默认日期格式*/
    private static final String DEFAULT_DATE = "yyyy-MM-dd";
    /**默认日期 时分秒格式*/
    private static final String DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static String nowDate() {
        GregorianCalendar calenda = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE);
        return sdf.format(calenda.getTime());
    }

    public static String now() {
        GregorianCalendar calenda = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME);
        return sdf.format(calenda.getTime());
    }

    public static String timeFormatUtil(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return sdf.format(time);
    }

    public static String timeFormatToFinalUtil(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        return sdf.format(time);
    }

    public static String currentDate(String format) {
        GregorianCalendar calenda = new GregorianCalendar();
        SimpleDateFormat sdf = null;
        if ((null == format) || ("".equals(format)))
            sdf = new SimpleDateFormat(DEFAULT_DATE);
        else {
            sdf = new SimpleDateFormat(format);
        }
        return sdf.format(calenda.getTime());
    }

    public static Date addDay(Date inDate, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(inDate);
        calendar.add(5, day);
        return calendar.getTime();
    }

    public static Date addDay(Date inDate, int day, int second) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(inDate);
        calendar.add(5, day);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, second);
        return calendar.getTime();
    }

    public static String addDay(String strDate, String pattern, int day) {
        return date2Str(addDay(str2Date(strDate, pattern), day), pattern);
    }

    public static String addDay(Date date, String pattern, int day) {
        return date2Str(addDay(date, day), pattern);
    }

    public static String date2Str(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date str2Date(String dateStr, String pattern) {
        try {
            DateFormat parser = new SimpleDateFormat(pattern);
            return parser.parse(dateStr);
        } catch (ParseException ex) {
        }
        return null;
    }

    public static int getWeek() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(7) - 1);
    }

    public static String getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(1) + "-" + (calendar.get(2) + 1);
    }

    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (((year % 100 != 0) || (year % 400 == 0))));
    }

    public static boolean isDateBefore(String date2) {
        try {
            Date date1 = new Date();
            DateFormat df = DateFormat.getDateTimeInstance();
            return date1.before(df.parse(date2));
        } catch (ParseException e) {
        }
        return false;
    }

    public static boolean isDateBefore(String date1, String date2) {
        try {
            DateFormat df = DateFormat.getDateTimeInstance();
            return df.parse(date1).before(df.parse(date2));
        } catch (ParseException e) {
        }
        return false;
    }

    public static String getDate(long d, String pattern) {
        Date c = new Date(d);
        return date2Str(c, pattern);
    }

    public static long dayDiff(Date date1, Date date2) {
        return ((date2.getTime() - date1.getTime()) / 86400000L);
    }

    public static String getAstro(String birth) {
        int month = Integer.parseInt(birth.substring(birth.indexOf("-") + 1,
                birth.lastIndexOf("-")));
        int day = Integer.parseInt(birth.substring(birth.lastIndexOf("-") + 1));
        String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
        int[] arr = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
        int start = month * 2 - ((day < arr[(month - 1)]) ? 2 : 0);
        return s.substring(start, start + 2) + "座";
    }

    public static boolean isDate(String date) {
        StringBuffer reg = new StringBuffer(
                "^((\\d{2}(([02468][048])|([13579][26]))-?((((0?");
        reg.append("[13578])|(1[02]))-?((0?[1-9])|([1-2][0-9])|(3[01])))");
        reg.append("|(((0?[469])|(11))-?((0?[1-9])|([1-2][0-9])|(30)))|");
        reg.append("(0?2-?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12");
        reg.append("35679])|([13579][01345789]))-?((((0?[13578])|(1[02]))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(30)))|(0?2-?((0?[");
        reg.append("1-9])|(1[0-9])|(2[0-8]))))))");
        Pattern p = Pattern.compile(reg.toString());
        return p.matcher(date).matches();
    }


    /**
     * 获取日期本月最后一天
     *
     * @param date
     * @return
     */
    public static Date lastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获取日期本月最后一天
     *
     * @param date
     * @return
     */
    public static Date lastOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }


    /**
     * 获取日期本月第一天
     *
     * @param date
     * @return
     */
    public static Date fristDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取日期下个月的第一天
     *
     * @param date
     * @return
     */
    public static Date fristDayOfNextMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 每天零点时间戳
     *
     * @return
     */
    public static long getTimeOfZero() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTimeInMillis() / 1000;
    }
}