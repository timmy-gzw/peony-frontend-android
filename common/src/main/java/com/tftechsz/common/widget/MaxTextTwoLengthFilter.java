package com.tftechsz.common.widget;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;

import com.tftechsz.common.R;
import com.tftechsz.common.utils.Utils;

/**
 * 数字英文算一位，中文算两位，超出位数弹吐司
 */
public class MaxTextTwoLengthFilter implements InputFilter {
    private static final int ASCII = 1; //英文字符长度
    private static final int OTHER = 2; //其他字符长度

    private final int mMaxLength; //可输入的最大长度
    private Context context;

    public MaxTextTwoLengthFilter(int maxLength, Context context) {
        this.mMaxLength = maxLength;
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int strLength = countStringLength(dest);
        int keep = mMaxLength - strLength; //计算剩余可输入的长度
        if (keep <= 0) { //剩余可输入的长度不大于0，返回空字符
            Utils.toast(context.getString(R.string.more100));
            return "";
        } else if (keep >= countStringLength(source)) { //剩余可输入的长度大于输入字符串的长度，不做过滤处理，使用原来的输入字符串
            return null;
        } else { //输入字符串的长度大于剩余可输入的长度
            Utils.toast(context.getString(R.string.more100));
            int index = countCutIndex(keep, source); //计算截断输入字符串的位置
            return source.subSequence(0, index); //返回截断后的字符串
        }
    }

    /**
     * 计算输入字符串的截断位置
     */
    private int countCutIndex(int keep, CharSequence source) {
        int length = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            length += countCharLength(c);
            if (length > keep) {
                return i;
            }
        }
        return source.length() - 1;
    }

    /**
     * 计算字符串的长度
     */
    private int countStringLength(CharSequence str) {
        if (str == null) {
            return 0;
        }

        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            length += countCharLength(c);
        }
        return length;
    }

    /**
     * 计算字符的长度
     */
    private int countCharLength(char c) {
        if (33 <= c && c <= 126) { //英文字符值的范围
            return ASCII;
        } else {
            return OTHER;
        }
    }
}
