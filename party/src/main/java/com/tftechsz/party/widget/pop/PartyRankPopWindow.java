package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.google.gson.Gson;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.X5WebView;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;

import static com.tftechsz.common.base.BaseWebViewActivity.EXTRA_FROM_TYPE;
import static com.tftechsz.common.base.BaseWebViewActivity.EXTRA_URL_BANNER_INDEX;

/**
 * party魅力榜单
 */
public class PartyRankPopWindow extends BaseBottomPop {
    private X5WebView mWebView;
    private String link;
    private final UserProviderService service;
    private int mRoomId;

    public PartyRankPopWindow(Context context, String link, int roomId) {
        super(context);
        this.link = link;
        mRoomId = roomId;
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initUI();
    }

    private void initUI() {
        mWebView = findViewById(R.id.webView);
        initWebView();
        //增加统一参数 后台配置的url 设置默认0
        link += (link.contains("?") ? "&" : "?") + EXTRA_FROM_TYPE + "=20" + "&" + EXTRA_URL_BANNER_INDEX + "=" + 0;
        mWebView.loadUrl(Utils.performUrl(link));
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
    }


    /**
     * 定义JS需要调用的方法
     */
    public class AndroidtoJs extends Object {
        /**
         * token
         */
        @JavascriptInterface
        public String getNativeToken() {
            return service.getToken();
        }


        /**
         * token
         */
        @JavascriptInterface
        public int getRomId() {
            return mRoomId;
        }

        /**
         * token
         */
        @JavascriptInterface
        public String getUserInfo() {
            UserInfo userInfo = new UserInfo();
            userInfo.setUser_id(service.getUserId());
            userInfo.setSex(service.getUserInfo() == null ? 0 : service.getUserInfo().getSex());
            userInfo.setNickname(service.getUserInfo() == null ? "" : service.getUserInfo().getNickname());
            userInfo.setUser_code(service.getUserInfo() == null ? "" : service.getUserInfo().getUser_code());
            userInfo.setIcon(service.getUserInfo() == null ? "" : service.getUserInfo().getIcon());
            userInfo.setDesc(AppUtils.getAppName() + AppUtils.getAppVersionName());
            return new Gson().toJson(userInfo);
        }

    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_party_rank);
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

}

