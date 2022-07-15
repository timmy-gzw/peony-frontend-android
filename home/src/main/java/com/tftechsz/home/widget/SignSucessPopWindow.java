package com.tftechsz.home.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.home.R;

/**
 * 包 名 : com.tftechsz.home.widget
 * 描 述 : TODO
 */
public class SignSucessPopWindow extends BaseCenterPop {

    private TextView mTvGold;

    public SignSucessPopWindow(Context context) {
        super(context);
        initUI();
    }

    private void initUI() {
        mTvGold = findViewById(R.id.tv_title);
        findViewById(R.id.ic_close).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_sign_in_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2022/7/15 领取更多积分
                ToastUtil.showToast(getContext(), "敬请期待");
                dismiss();
            }
        });
        setOutSideDismiss(false);
    }

    public SignSucessPopWindow showPop(String gold) {
        mTvGold.setText(getContext().getString(R.string.sign_in_success_format, gold));
        showPopupWindow();
        return this;
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_sign_sucess);
    }
}
