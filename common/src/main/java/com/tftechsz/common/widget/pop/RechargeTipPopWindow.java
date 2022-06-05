package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.R;


/**
 *  充值提示弹窗
 */
public class RechargeTipPopWindow extends BaseCenterPop implements View.OnClickListener {

    private TextView mTvContent;
    private TextView mTvRecharge;
    private TextView mTvLeft;


    public RechargeTipPopWindow(Context context) {
        super(context);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        mTvRecharge = findViewById(R.id.tv_recharge);
        mTvRecharge.setOnClickListener(this);
        mTvLeft = findViewById(R.id.tv_left);
        mTvLeft.setOnClickListener(this);
        mTvContent = findViewById(R.id.tv_content);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_recharge_pay);
    }

    public void setContent(String content) {
        mTvContent.setText(content);
    }


    public void setLeftButton(String leftText) {
        mTvLeft.setText(leftText);
    }

    public void setRightButton(String rightText) {
        mTvRecharge.setText(rightText);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_recharge) {
            if (listener != null)
                listener.onSure();
            dismiss();
        } else if (id == R.id.tv_left) {
            if (listener != null)
                listener.onCancel();
            dismiss();
        }
    }

    public interface OnSelectListener {

        void onCancel();

        void onSure();

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
