package com.tftechsz.common.widget.chat;

import android.annotation.SuppressLint;

import com.tftechsz.common.utils.TimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ChatTimeUtils {

    public static String getFlirtCardTime(long time) {
        long times = (System.currentTimeMillis() - time) / 1000;//时间差
        long temp = 0;
        if (times / 60 < 1) {
            return "刚刚";
        } else if ((temp = times / 60) < 60) {
            return temp + "分钟前";
        } else if ((temp = temp / 60) < 24) {
            return temp + "小时前";
        } else if ((temp = temp / 24) < 30) {
            return temp + "天前";
        } else if ((temp = temp / 30) < 12) {
            return temp + "月前";
        } else {
            return temp / 12 + "年前";
        }
    }


    public static String getTimeStringAutoShort(Date srcDate) {
        String ret = "";

        try {
            GregorianCalendar gcCurrent = new GregorianCalendar();
            gcCurrent.setTime(new Date());
            int currentYear = gcCurrent.get(1);
            int currentMonth = gcCurrent.get(2);
            int currentDay = gcCurrent.get(5);
            GregorianCalendar gcSrc = new GregorianCalendar();
            gcSrc.setTime(srcDate);
            int srcYear = gcSrc.get(1);
            int srcMonth = gcSrc.get(2);
            int srcDay = gcSrc.get(5);
            if (currentYear == srcYear) {
                if (currentMonth == srcMonth && currentDay == srcDay) {
                    ret = getTimeString(srcDate, "HH:mm");
                } else {
                    ret = getTimeString(srcDate, "MM-dd HH:mm");
                }
            } else {
                ret = getTimeString(srcDate, "yyyy-MM-dd HH:mm");
            }
        } catch (Exception var10) {
            System.err.println("【DEBUG-getTimeStringAutoShort】计算出错：" + var10.getMessage() + " 【NO】");
        }

        return ret;
    }


    public static long getTime(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ", java.util.Locale.US);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
        df2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null)
            return 0;
        return date.getTime();
    }


    public static String getTimeString(Date dt, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(dt);
        } catch (Exception var3) {
            return "";
        }
    }

    private static final String[] WEED_DAY = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static String getChatTime(long timesamp) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();//当前日期
        Calendar otherCalendar = Calendar.getInstance();//消息日期
        otherCalendar.setTimeInMillis(timesamp);

        String dayTimeFormat = "HH:mm";
        String otherTimeFormat = "M月d日 HH:mm";
        String yearTimeFormat = "yyyy年M月d日 HH:mm";
        String am_pm = "";
        int hourOfDay = otherCalendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 0 && hourOfDay < 6) {
            am_pm = "凌晨";
        } else if (hourOfDay >= 6 && hourOfDay < 12) {
            am_pm = "上午";
        } else if (hourOfDay == 12) {
            am_pm = "中午";
        } else if (hourOfDay > 12 && hourOfDay < 18) {
            am_pm = "下午";
        } else if (hourOfDay >= 18) {
            am_pm = "晚上";
        }
        dayTimeFormat =  "HH:mm";
        otherTimeFormat = "M-d";
        yearTimeFormat = "yyyy-M-d";

        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {//同年
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//同月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = "" + getHourAndMin(timesamp, dayTimeFormat);
                        break;
                    case 1:
                        result = "昨天";   //+ getHourAndMin(timesamp, dayTimeFormat)
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
//                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
//                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
//                        if (dayOfMonth == todayOfMonth) {//同周
//                            int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
//                            if (dayOfWeek != 1) {//判断当前是不是星期日（此处将周日理解为一周第一天）   如想显示为：周日 12:09 可去掉此判断
//                                result = WEED_DAY[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHourAndMin(timesamp, dayTimeFormat);
//                            } else {
//                                result = getDiffWeekTime(timesamp, otherTimeFormat);
//                            }
//                        } else {
//                            result = getDiffWeekTime(timesamp, otherTimeFormat);
//                        }
                        result = getDiffWeekTime(timesamp, otherTimeFormat);
                        break;
                    default://相差一周以上
                        result = getDiffWeekTime(timesamp, otherTimeFormat);
                        break;
                }
            } else {
                result = getDiffWeekTime(timesamp, otherTimeFormat);
            }
        } else {
            result = getYearTime(timesamp, yearTimeFormat);
        }


        if (result.startsWith("0")) {
            return result.substring(1, result.length());
        } else {
            return result;
        }
    }


    /**
     * 当天的显示时间格式
     *
     * @param timesamp 消息时间戳
     * @return
     */
    private static String getHourAndMin(long timesamp, String timeFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(timesamp));
    }

    /**
     * 不同周的显示时间格式
     *
     * @param timesamp
     * @param timeFormat
     * @return
     */
    private static String getDiffWeekTime(long timesamp, String timeFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(timesamp));
    }

    /**
     * 不同年的显示时间格式
     *
     * @param timesamp       消息时间戳
     * @param yearTimeFormat 格式化标准
     * @return
     */
    private static String getYearTime(long timesamp, String yearTimeFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(timesamp));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCommentTime(long timestamp) {
        Calendar todayCalendar = Calendar.getInstance();//当前日期
        Calendar otherCalendar = Calendar.getInstance();//消息日期
        otherCalendar.setTimeInMillis(timestamp);
        String format;
        if (todayCalendar.get(Calendar.DATE) == otherCalendar.get(Calendar.DATE) &&
                todayCalendar.getTimeInMillis() - timestamp <= 1000 * 60 * 60 * 24) {
            // 当天
            format = "HH:mm";
            return new SimpleDateFormat(format).format(new Date(timestamp));
        } else if (isYesterday(todayCalendar, otherCalendar)) {
            // 昨天
            format = "昨天 HH:mm";
            return new SimpleDateFormat(format).format(new Date(timestamp));
        } else if (Integer.parseInt(TimeUtils.getCurrentDay()) - Integer.parseInt(TimeUtils.getDayFromTime(timestamp)) == 2) {
            // 2天前
            return "2天前";
        } else if (Integer.parseInt(TimeUtils.getCurrentDay()) - Integer.parseInt(TimeUtils.getDayFromTime(timestamp)) == 3) {
            // 3天前
            return "3天前";
        } else if (todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR)) {
            format = "MM月dd日";
            return new SimpleDateFormat(format).format(new Date(timestamp));
        } else {
            format = "yyyy年M月d日";
            return new SimpleDateFormat(format).format(new Date(timestamp));
        }
    }

    private static boolean isYesterday(Calendar todayCalendar, Calendar othCalendar) {
        // 时间在2天之内
        boolean timeStampDistance = todayCalendar.getTimeInMillis() - othCalendar.getTimeInMillis() < 1000 * 60 * 60 * 24 * 2;
        if (!timeStampDistance) return false;
        // 同月，间隔一天
        if (todayCalendar.get(Calendar.MONTH) == othCalendar.get(Calendar.MONTH) &&
                todayCalendar.get(Calendar.DATE) - othCalendar.get(Calendar.DATE) == 1) {
            return true;
        }
        // 不同月，间隔一天
        if (todayCalendar.get(Calendar.MONTH) - othCalendar.get(Calendar.MONTH) == 1
                && (todayCalendar.get(Calendar.DATE) == 1
                && othCalendar.get(Calendar.DATE) == getMonthOfDay(othCalendar.get(Calendar.YEAR), othCalendar.get(Calendar.MONTH) + 1))) {
            return true;
        }
        // 不同年，间隔一天
        if (todayCalendar.get(Calendar.YEAR) - othCalendar.get(Calendar.YEAR) == 1
                && (todayCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY && othCalendar.get(Calendar.MONTH) == Calendar.DECEMBER)
                && (todayCalendar.get(Calendar.DATE) == 1 && othCalendar.get(Calendar.DATE) == getMonthOfDay(othCalendar.get(Calendar.YEAR), othCalendar.get(Calendar.MONTH) + 1))) {
            return true;
        }
        return false;
    }

    public static int getMonthOfDay(int year, int month) {
        int day = 0;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            day = 29;
        } else {
            day = 28;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;

        }

        return 0;
    }
}
