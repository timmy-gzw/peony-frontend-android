package com.netease.nim.uikit.common.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EditTextClipboard extends androidx.appcompat.widget.AppCompatEditText {
    public EditTextClipboard(Context context) {
        super(context);
    }

    public EditTextClipboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextClipboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
//        if (id == android.R.id.paste) {
//            //调用剪贴板
//            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//            //改变剪贴板中Content
//            if (clip != null && !TextUtils.isEmpty(clip.toString()) && StringUtils.isTrimEmpty(clip.getText().toString())) {
//                clip.setText("");
//            } else if (!stringFilter(clip.getText().toString()) && !ChinesstringFilter(clip.getText().toString())) {
//                clip.setText("");//解决全角空格还能发送问题
//            }
//        }
        return super.onTextContextMenuItem(id);
    }

    public boolean stringFilter(String str) {
// 只允许字母、数字、英文空白字符
        String regEx = "[^a-zA-Z0-9\\s]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean ChinesstringFilter(String str) throws PatternSyntaxException {
        String regEx = "^[\\u4e00-\\u9fa5]+$"; //要过滤掉的字符 -只要中文
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
