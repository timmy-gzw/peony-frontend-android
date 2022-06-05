package com.tftechsz.common.widget;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChineseFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
      /*  for (int i = start; i < end; i++) {
            if (!isChinese(source.charAt(i))) {
                return "";
            }
        }*/
        if (!StringFilter(source.toString())) {
            return "";
        }
//        return StringFilter(source.toString());
//        return null;
        return null;
    }

    /**
     * 只能输入中文的判断
     *
     * @param c
     * @return
     */
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean StringFilter(String str) throws PatternSyntaxException {
        String regEx = "^[\\u4e00-\\u9fa5]+$"; //要过滤掉的字符 -只要中文
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
