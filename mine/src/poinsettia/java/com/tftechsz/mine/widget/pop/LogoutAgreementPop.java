package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.net.http.SslError;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.X5WebView;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.mine.R;

/**
 * 包 名 : com.tftechsz.mine.widget.pop
 * 描 述 : TODO
 */
public class LogoutAgreementPop extends BaseCenterPop {
    private X5WebView mWebView;
    private final String url;
    private final AgreeCallBack callBack;

    public LogoutAgreementPop(Context context, String url, AgreeCallBack callBack) {
        super(context);
        this.url = url;
        this.callBack = callBack;
        initUI();
    }

    private void initUI() {
        mWebView = findViewById(R.id.webView);
        initWebView();
        mWebView.loadUrl(Utils.performUrl(url));
        findViewById(R.id.iv_close).setOnClickListener(v->dismiss());
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
        mWebView.addJavascriptInterface(new AndroidtoJs(), "android");

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 兼容https
            }
        });
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

    /**
     * 定义JS需要调用的方法
     */
    public class AndroidtoJs {
        @JavascriptInterface
        public void agreeLogout() {
            Utils.runOnUiThread(() -> {
                getPopupWindow().dismiss();
                callBack.call();
            });
        }
    }

    public interface AgreeCallBack {
        void call();

    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_logout_agreement);
    }
}
