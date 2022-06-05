package com.tftechsz.home.widget;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.home.R;
import com.tftechsz.common.widget.pop.BaseCenterPop;

/**
 * 包 名 : com.tftechsz.home.widget
 * 描 述 : TODO
 */
public class SignSucessPopWindow extends BaseCenterPop {

    private TextView mTvGold;
    private Handler mHandler = new Handler();

    public SignSucessPopWindow(Context context) {
        super(context);
        initUI();
    }

    private void initUI() {
        mTvGold = findViewById(R.id.tv_gold);
        setOutSideDismiss(false);
    }

    public SignSucessPopWindow showPop(String gold) {
        mTvGold.setText(gold);
        showPopupWindow();
        mHandler.postDelayed(() -> dismiss(), 1500);
        return this;
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_sign_sucess);
    }
}
