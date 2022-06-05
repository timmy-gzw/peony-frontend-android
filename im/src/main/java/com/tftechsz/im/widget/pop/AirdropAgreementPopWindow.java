package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.im.R;
import com.tftechsz.common.Constants;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.X5WebView;
import com.tftechsz.common.widget.pop.BaseBottomPop;

/**
 * 召唤空投
 */
public class AirdropAgreementPopWindow extends BaseBottomPop implements View.OnClickListener {
    private X5WebView mWebView;
    private final UserProviderService service;

    public AirdropAgreementPopWindow(Context context) {
        super(context);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.iv_close).setOnClickListener(this);
        mWebView = findViewById(R.id.webView);
        initWebView();
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.api != null) {
            String url = configInfo.api.airdropintd + "?__app_name=" + Constants.WEB_PARAMS_APP_NAME;
            mWebView.loadUrl(Utils.performUrl(url));
        }
    }


    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
//        settings.setLayoutAlgorithm(IX5WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_airdrop_agreement);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        }
    }
}

