package com.tftechsz.im.widget;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.tftechsz.im.widget.pop.VoiceNoticePopWindow;

import razerdp.basepopup.BasePopupWindow;

public class FamilyManagerClickSpan extends ClickableSpan {

    private final BasePopupWindow pop;
    private String str;
    private VoiceNoticePopWindow.OnSelectListener onSelectListener;

    public FamilyManagerClickSpan(BasePopupWindow pop, String str, VoiceNoticePopWindow.OnSelectListener listener) {
        this.str = str;
        this.pop = pop;
        this.onSelectListener = listener;
    }

    @Override
    public void onClick(View widget) {
        if (pop != null) {
            pop.dismiss();
        }
        if (onSelectListener != null) {
            onSelectListener.clickEdit(str);
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
    }
}
