package com.tftechsz.mine.widget;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.common.BuildConfig;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.iservice.UserProviderService;

/**
 * 协议点击和改变字体颜色
 */

public class TextClick extends ClickableSpan {
    UserProviderService service;
    int type;
    private final Context mContext;

    public TextClick(Context context, int type) {
        this.type = type;
        mContext = context;
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @Override
    public void onClick(View widget) {
        String link;
        String title;
        if (type == 0) {
            link = (BuildConfig.DEBUG ? Constants.HOST_H5_DEV : Constants.HOST_H5) + "agreement.html?__app_name=" + Constants.WEB_PARAMS_APP_NAME;
            title = "用户协议";
            BaseWebViewActivity.start(mContext, title, link, 0, 8);
        } else {
            link = (BuildConfig.DEBUG ? Constants.HOST_H5_DEV : Constants.HOST_H5) + "policy.html?__app_name=" + Constants.WEB_PARAMS_APP_NAME;
            title = "隐私政策";
            BaseWebViewActivity.start(mContext, title, link, 0, 9);
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
    }
}
