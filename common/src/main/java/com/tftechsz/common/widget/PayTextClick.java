package com.tftechsz.common.widget;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.base.BaseWebViewActivity;

/**
 * 支付协议点击和改变字体颜色
 */

public class PayTextClick extends ClickableSpan {
    private final ConfigInfo.MineInfo mineInfo;
    private final Context mContext;

    public PayTextClick(Context context, ConfigInfo.MineInfo mineInfo) {
        this.mineInfo = mineInfo;
        mContext = context;
    }

    @Override
    public void onClick(View widget) {
        String link = mineInfo.link;
        String title = mineInfo.title;
        ConfigInfo.Option option = mineInfo.option;
        String webView = "webview://";
        if (link.startsWith(webView)) {
            boolean isShowBar = true;
            if (option != null && option.is_topbar == 0) {
                isShowBar = false;
            }
            BaseWebViewActivity.start(mContext, title, link.substring(webView.length()), isShowBar, 0, 0);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
    }
}
