package com.tftechsz.home.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.home.R;
import com.tftechsz.home.entity.SignInSuccessBean;

/**
 * 包 名 : com.tftechsz.home.widget
 * 描 述 : TODO
 */
public class SignSucessPopWindow extends BaseCenterPop {

    private TextView tvTitle;
    private TextView tvMore;
    private SignInSuccessBean bean;

    public SignSucessPopWindow(Context context) {
        super(context);
        initUI();
    }

    private void initUI() {
        tvTitle = findViewById(R.id.tv_title);
        tvMore = findViewById(R.id.tv_sign_in_more);
        findViewById(R.id.ic_close).setOnClickListener(v -> dismiss());
        tvMore.setOnClickListener(v -> {
            if (bean != null && bean.getLinkBean() != null) {
                CommonUtil.performLink(getContext(), new ConfigInfo.MineInfo(bean.getLinkBean().getLink(), bean.getLinkBean().getOption()));
            }
            dismiss();
        });
        setOutSideDismiss(false);
    }

    public SignSucessPopWindow showPop(SignInSuccessBean data) {
        this.bean = data;
        if (data != null) {
            String text = data.getTitle() + data.getCost();
            tvTitle.setText(text);
            if (data.getLinkBean() != null) {
                tvMore.setVisibility(TextUtils.isEmpty(data.getLinkBean().getLink()) ? View.GONE : View.VISIBLE);
            }
        }
        showPopupWindow();
        return this;
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_sign_sucess);
    }
}
