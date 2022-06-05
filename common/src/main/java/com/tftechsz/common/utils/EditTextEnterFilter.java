package com.tftechsz.common.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 包 名 : com.tftechsz.common.utils
 * 描 述 : TODO
 */
public class EditTextEnterFilter implements InputFilter {
    private final boolean isFilter;

    public EditTextEnterFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }

    @Override
    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
        boolean bool = src.equals("\n");
        if (isFilter && bool) {
            return dest.subSequence(dstart, dend);
        }
        return dest.subSequence(dstart, dstart) + src.toString();
    }
}
