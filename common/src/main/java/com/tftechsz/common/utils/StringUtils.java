package com.tftechsz.common.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static DecimalFormat df = new DecimalFormat("###.#");

    public static String formatNumbers(float originNumber) {
        if (originNumber > 1000) {
            double current = new BigDecimal(originNumber).divide(new BigDecimal(1000), 1, BigDecimal.ROUND_DOWN).doubleValue();
            return df.format(current) + "K";
        } else {
            return df.format(originNumber);
        }
    }

    /**
     * 判断是否是手机号
     */
    public static boolean isPhone(String str) {
        Pattern p;
        Matcher m;
        boolean b;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    /**
     * 隐藏电话号码
     */
    public static String hintPhone(String phone) {
        if (!TextUtils.isEmpty(phone) && phone.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < phone.length(); i++) {
                char c = phone.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return "";
    }


    /**
     * 限制字符长度
     *
     * @param str
     * @param maxLen
     * @return
     */
    public static String handleText(String str, int maxLen) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int count = 0;
        int endIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);
            if (item < 128) {
                count++;
            } else {
                count += 2;
            }
            if (maxLen == count || (item >= 128 && maxLen + 1 == count)) {
                endIndex = i;
            }
        }
        if (count <= maxLen) {
            return str;

        } else {
            return str.substring(0, endIndex) + "";
        }
    }

    /**
     * 判断字符长度
     *
     * @param text
     * @return
     */
    public static int judgeTextLength(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char item = text.charAt(i);
            if (item < 128) {
                count++;
            } else {
                count += 2;
            }
        }
        return count;
    }


    /**
     * 判断奇数偶数
     * @param userId
     * @return
     */
    public static boolean isEven(int userId) {
        if ((userId & 1) == 1) {
            return true;   //奇数
        } else
            return false;
    }

}
