package com.tftechsz.moment.widget;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.moment.R;

/**
 *  分享后保存图片后的弹窗
 */
public class ShareSecondPopWindow extends BaseCenterPop implements View.OnClickListener {

    public ShareSecondPopWindow(Context context) {
        super(context);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        findViewById(R.id.iv_close).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        }


    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_second_share);
    }


    public interface OnSelectListener {
        void chooseType(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
