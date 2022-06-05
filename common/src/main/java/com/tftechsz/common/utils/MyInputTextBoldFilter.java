package com.tftechsz.common.utils;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 包 名 : com.tftechsz.common.utils
 * 描 述 : TODO
 */
public class MyInputTextBoldFilter implements TextWatcher {
    private final EditText mEditText;
    private TextChangedCallBack mCallBack;
    private final Typeface normal = Typeface.defaultFromStyle(Typeface.NORMAL);
    private final Typeface bold = Typeface.defaultFromStyle(Typeface.BOLD);

    public MyInputTextBoldFilter(EditText editText) {
        mEditText = editText;
        mEditText.addTextChangedListener(this);
    }

    public MyInputTextBoldFilter(EditText editText, TextChangedCallBack mCallBack) {
        mEditText = editText;
        this.mCallBack = mCallBack;
        mEditText.addTextChangedListener(this);
    }

    public interface TextChangedCallBack {
        void textChange();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mCallBack != null) {
            mCallBack.textChange();
        }
        if (TextUtils.isEmpty(Utils.getText(mEditText))) {
            mEditText.setTypeface(normal);
        } else {
            mEditText.setTypeface(bold);

        }
    }
}
