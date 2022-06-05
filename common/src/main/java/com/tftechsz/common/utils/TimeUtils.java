package com.tftechsz.common.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static final String DEFAULT_DATE_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_STRING_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public static String getMovieDuration(int duration) {
        int min = duration / 60;
        int second = duration % 60;
        String m;
        String s;
        if (min < 10) {
            m = "0" + min;
        } else {
            m = min + "";
        }
        if (second < 10) {
            s = "0" + second;
        } else {
            s = second + "";
        }
        return m + ":" + s;
    }

    public static String long2String(long time) {
        //毫秒转秒
        int sec = (int) time / 1000;
        int min = sec / 60;    //分钟
        sec = sec % 60;        //秒
        if (min < 10) {    //分钟补0
            if (sec < 10) {    //秒补0
                return "0" + min + ":0" + sec;
            } else {
                return "0" + min + ":" + sec;
            }
        } else {
            if (sec < 10) {    //秒补0
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        }
    }

    //秒 转 00:00:00
    public static String int2String(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        String hours = null;
        String mins = null;
        String sec = null;
        if (h >= 10) {
            hours = String.valueOf(h);
        } else if (h > 0 && h < 10) {
            hours = "0" + h;
        } else {
            hours = "00";
        }

        if (d >= 10) {
            mins = ":" + d;
        } else if (d > 0 && d < 10) {
            mins = ":0" + d;
        } else {
            mins = ":00";
        }

        if (s >= 10) {
            sec = ":" + s;
        } else if (s > 0 && s < 10) {
            sec = ":0" + s;
        } else {
            sec = ":00";
        }
        return hours + mins + sec;
    }


    //秒 转 00:00:00
    public static String l2String(long second) {
        long h = 0;
        long d = 0;
        long s = 0;
        long temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        String hours = null;
        String mins = null;
        String sec = null;
        if (h >= 10) {
            hours = String.valueOf(h);
        } else if (h > 0 && h < 10) {
            hours = "0" + h;
        } else {
            hours = "00";
        }

        if (d >= 10) {
            mins = ":" + d;
        } else if (d > 0 && d < 10) {
            mins = ":0" + d;
        } else {
            mins = ":00";
        }

        if (s >= 10) {
            sec = ":" + s;
        } else if (s > 0 && s < 10) {
            sec = ":0" + s;
        } else {
            sec = ":00";
        }
        return hours + mins + sec;
    }


    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    public static String getTimeYMD(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    public static String getCurrentDateInString() {
        return getTimeYMD(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * 是否今天
     *
     * @param inputJudgeDate
     * @return
     */
    public static boolean isToday(Date inputJudgeDate) {
        boolean flag = false;
        //获取当前系统时间
        long longDate = System.currentTimeMillis();
        Date nowDate = new Date(longDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(nowDate);
        String subDate = format.substring(0, 10);
        //定义每天的24h时间范围
        String beginTime = subDate + " 00:00:00";
        String endTime = subDate + " 23:59:59";
        Date paseBeginTime = null;
        Date paseEndTime = null;
        try {
            paseBeginTime = dateFormat.parse(beginTime);
            paseEndTime = dateFormat.parse(endTime);

        } catch (ParseException e) {
        }
        if (inputJudgeDate.after(paseBeginTime) && inputJudgeDate.before(paseEndTime)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 当前时间是否是某天
     */
    public static boolean isHopeDate(String yydm) {
        //获取当前系统时间
        long longDate = System.currentTimeMillis();
        Date nowDate = new Date(longDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String format = dateFormat.format(nowDate);
        return format.equals(yydm);
    }

    public static String dateToYYYYMMdd(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        String sDate = sdf.format(date);
        return sDate;
    }


    public static String dateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String sDate = sdf.format(date);
        return sDate;
    }

    public static String StringFormat(long date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String sDate = sdf.format(date);
        return sDate;
    }


    //2019-09-20T09:53:32Z
    public static String dealDateFormat(String oldDate) {
        try {
            DateFormat df = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss'Z'");
            Date date = df.parse(oldDate);
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            String sDate = sdf.format(date);
            return sDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    //2019-09-20T09:53:32Z
    public static String dealInstanToYYYYMMDD(String oldDate) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            Date date = df.parse(oldDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            String sDate = sdf.format(date);
            return sDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    //2019-09-20T09:53:32Z
    public static String dealInstanToYYYYMMDDWithPoint(String oldDate) {
        try {
            Date date = new Date(Long.parseLong(oldDate));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
            String sDate = sdf.format(date);
            return sDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int culSurplusDay(long time) {
        long surplusTime = time - System.currentTimeMillis();
        int surplusDay = (int) (surplusTime / 1000 / 60 / 60 / 24);
        if (surplusDay < 1) {
            surplusDay = 1;
        }
        return surplusDay;
    }

    //2019-09-20T09:53:32Z  2019-09-21T07:06:17Z
    public static long dealDateFormatTolong(String oldDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);//注意格式化的表达式
        Date d = null;
        try {
            d = format.parse(oldDate);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public static long dealDateFormatTolongForUpload(String oldDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);//注意格式化的表达式
        Date d = null;
        try {
            d = format.parse(oldDate);
            return d.getTime() + 8 * 60 * 60 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    //2019-09-20T09:53:32Z  2019-09-21T07:06:17Z
    public static String dealDateFormatToRecord(String oldDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);//注意格式化的表达式
        Date d = null;
        try {
            d = format.parse(oldDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String sDate = sdf.format(d.getTime() + 8 * 3600 * 1000);
            return sDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    //
    public static String longToString(long oldDate) {
        Date d = null;
        try {
            d = new Date(oldDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sDate = sdf.format(d.getTime());
            return sDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    // 2020-01-02T11:12:22.000+0000  to  2020/01/02 19:12:22
    public static String dealInstan(String data) {
        return dealInstanFormat(data, "yyyy-MM-dd");
    }

    public static String dealInstanFormat(String data, String Format) {
        if (data.contains(".")) {
            data = data.substring(0, data.indexOf(".")) + "Z";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//注意格式化的表达式
        Date d = null;
        try {
            d = format.parse(data);
            SimpleDateFormat sdf = new SimpleDateFormat(Format);
            String sDate = sdf.format(d.getTime() + 8 * 3600 * 1000);
            return sDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 计算秒差
     *
     * @param date1
     * @param date2
     * @return double
     */
    public static double timeCalculateSeconds(Date date1, Date date2) {
        double millisecond = date2.getTime() - date1.getTime();
        double seconds = millisecond / 1000;
        return seconds;
    }

    /**
     * 字符串转换成 Instant 格式
     *
     * @param time
     * @return
     */
    public static String stringToInstant(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.DEFAULT_DATE_STRING_SLASH);
        Date dateTime = null;
        String resultTime = "";
        try {
            dateTime = simpleDateFormat.parse(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            resultTime = format.format(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultTime;
    }


    public static String transData(String time, String pattern) {

        Instant now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = Instant.parse(time);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
            return dateTimeFormatter.format(now);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//注意格式化的表达式
            Date d = null;
            try {
                d = format.parse(time);
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                String sDate = sdf.format(d.getTime() + 8 * 3600 * 1000);
                return sDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String dateToInstant(Date d) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return format.format(d);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            Instant instant = Instant.ofEpochMilli(d.getTime());
//            return instant.toString();
//        } else {
//        }
    }

    /**
     * Instant 转换为时间戳
     *
     * @param s
     * @return
     */
    public static long instantToDate(String s) {
        Instant instant = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");//注意格式化的表达式
        Date d = null;
        try {
            d = format.parse(s);
            return d.getTime() + 8 * 3600 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        }
        return 0;
    }

    /**
     * 秒转换成 03:20这种格式
     *
     * @param time
     */
    public static String fromSecond(int time) {
        int min = time / 60;
        int second = time % 60;
        String m = "";
        String s = "";
        if (min < 10) {
            m = "0" + min;
        } else {
            m = min + "";
        }
        if (second < 10) {
            s = "0" + second;
        } else {
            s = second + "";
        }

        return m + ":" + s;
    }

    /**
     * 秒换算成时分秒
     *
     * @param second
     * @return
     */
    public static String cal(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        if (h > 0) {
            return h + "小时" + d + "分钟" + s + "秒";
        } else if (d > 0) {
            return d + "分钟" + s + "秒";
        } else {
            return s + "秒";
        }

    }

    /**
     * 秒换算成时分秒
     *
     * @param second
     * @return
     */
    public static String calT(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        if (h > 0) {
            return h + "时" + d + "分" + s + "秒";
        } else if (d > 0) {
            return d + "分" + s + "秒";
        } else {
            return s + "秒";
        }

    }

    /**
     * 秒换算成时分秒
     *
     * @param second
     * @return
     */
    public static String calY(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        if (h > 0) {
            return h + "′" + d + "′" + s + "″";
        } else if (d > 0) {
            return d + "′" + s + "″";
        } else {
            return s + "″";
        }

    }

    public static String calYSFM(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        if (h > 0) {
            return h + ":" + d + ":" + s + "″";
        } else if (d > 0) {
            return d + ":" + s + "″";
        } else {
            return s + "″";
        }

    }

    /**
     * 判断2个时间大小
     * yyyy-MM-dd HH:mm 格式（自己可以修改成想要的时间格式）
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static int timeCompare(String startTime, String endTime, String pattern) {
        int i = 0;
        //注意：传过来的时间格式必须要和这里填入的时间格式相同
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间
            // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            if (date2.getTime() < date1.getTime()) {
                //结束时间小于开始时间
                i = 1;
            } else if (date2.getTime() == date1.getTime()) {
                //开始时间与结束时间相同
                i = 2;
            } else if (date2.getTime() > date1.getTime()) {
                //结束时间大于开始时间
                i = 3;
            }
        } catch (Exception e) {

        }
        return i;

    }

    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.MONTH) + 1;
    }

    public static int getCurrentDay2() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static String getCurrentDay() {
        Calendar c = Calendar.getInstance();//
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
        return mDay + "";
    }


    public static String getAstro(int month, int day) {
        String[] starArr = {"魔羯座", "水瓶座", "双鱼座", "牡羊座",
                "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座"};
        int[] DayArr = {22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22};  // 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < DayArr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        if (index == 12) {
            index = 0;
        }
        return starArr[index];
    }

    public static String format2Hours(long seconds) {
        if (seconds > 0) {
            StringBuilder builder = new StringBuilder();
            long hours = seconds / 3600;
            if (hours > 0) return builder.append(hours).append("小时").toString();
            long min = seconds % 3600 / 60;
            if (min > 0) return builder.append(min).append("分钟").toString();
            return builder.toString();
        } else return "0";
    }

    /**
     * 将时间字符串转成年纪,只支持 "yyyy-MM-dd'T'HH:mm:ss'Z'" 格式
     *
     * @param birthdayStr 需要转化的时间字符串
     * @return 年龄
     */
    public static String getAge(String birthdayStr) {
        if (TextUtils.isEmpty(birthdayStr)) return "年龄未设置";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = format.parse(birthdayStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return String.format("%d岁", currentYear - year);
        } catch (Exception e) {
            return "年龄未设置";
        }
    }

    public static String date2Str(Date date, String fmt) {
        try {
            if (date == null) {
                return "";
            }
            SimpleDateFormat format = new SimpleDateFormat(fmt);
            return format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取本月第一天
     *
     * @return
     */
    public static Date getMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 0);
        return calendar.getTime();
    }

    /**
     * 时间戳中获取日
     *
     * @param time
     * @return
     */
    public static String getFromTime(long time) {
        Date d = new Date(time);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(d);
    }

    /**
     * 时间戳中获取日
     *
     * @param time
     * @return
     */
    public static String getDayFromTime(long time) {
        Date d = new Date(time);
        return new SimpleDateFormat("dd", Locale.US).format(d);
    }

    public static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60;
        long seconds = duration % 60;
        long second = Math.round((float) seconds);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }

}
