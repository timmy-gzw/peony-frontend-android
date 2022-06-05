package com.tftechsz.common.widget;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.blankj.utilcode.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (id == android.R.id.paste) {
            //调用剪贴板
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            //改变剪贴板中Content
            if (clip != null && !TextUtils.isEmpty(clip.getText()) && StringUtils.isTrimEmpty(clip.getText().toString())) {
                clip.setText("");
            }/* else if (clip != null && !TextUtils.isEmpty(clip.getText()) && !stringFilter(clip.getText().toString()) && !ChineseFilter.StringFilter(clip.getText().toString())) {
                clip.setText("");//解决全角空格还能发送问题
            }*/
        }
        return super.onTextContextMenuItem(id);
    }

    public boolean stringFilter(String str) {
// 只允许字母、数字、英文空白字符
        String regEx = "[^a-zA-Z0-9\\s]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
