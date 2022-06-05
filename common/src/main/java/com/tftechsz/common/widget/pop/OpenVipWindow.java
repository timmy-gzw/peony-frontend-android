package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.R;

/**
 *  VIP 开通弹窗
 */
public class OpenVipWindow extends BaseCenterPop implements View.OnClickListener {


    public OpenVipWindow(Context context) {
        super(context);
        mContext = context;
        initUI();
        setOutSideTouchable(false);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_vip_privacy);
    }

    private void initUI() {
        findViewById(R.id.iv_close).setOnClickListener(this);
        TextView openVip = findViewById(R.id.tv_open);
        openVip.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_open) {
            if (listener != null)
                listener.onSure();
            dismiss();
        } else if (id == R.id.iv_close) {
            dismiss();
        }
    }


    public interface OnSelectListener {

        void onSure();

    }

    public OnSelectListener listener;

    public OpenVipWindow addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
        return this;
    }
}
