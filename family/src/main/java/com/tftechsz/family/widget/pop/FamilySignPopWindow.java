package com.tftechsz.family.widget.pop;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.family.R;
import com.tftechsz.common.widget.pop.BaseCenterPop;

/**
 * 家族宣言弹窗
 */
public class FamilySignPopWindow extends BaseCenterPop implements View.OnClickListener {


    private final String mSign;  //宣言

    public FamilySignPopWindow(Activity context, String sign) {
        super(context);
        this.mSign = sign;
        setPopupFadeEnable(true);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.iv_close).setOnClickListener(this);
        TextView  mTvSign = findViewById(R.id.tv_family_sign);
        mTvSign.setText(mSign);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_sign);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        }
    }


}
