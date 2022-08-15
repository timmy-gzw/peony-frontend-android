package com.tftechsz.common.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.PathUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.ShareHelper;
import com.tftechsz.common.utils.SoftHideKeyBoardUtil;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.X5WebView;
import com.tftechsz.common.widget.pop.BasePayPopWindow;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.SignInPopWindow;
import com.tftechsz.common.widget.pop.SignSucessPopWindow;

import java.io.File;

public class BaseWebViewActivity extends BaseMvpActivity {
    private X5WebView mWebView;
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_SHOW_BAR = "extra_show_bar";
    private static final String EXTRA_TITLE_COLOR = "extra_title_color";   //title bar黑色 还是白色字体
    public static final String EXTRA_FROM_TYPE = "from_type";
    public static final String EXTRA_URL_BANNER_INDEX = "bannerIndex";
    /**
     * 拍照/选择文件请求码
     */
    private static final int REQ_CAMERA = 1;
    private static final int REQ_CHOOSE = REQ_CAMERA + 1;

    private final static int VIDEO_REQUEST = 120;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageArray;

    private static final String PATH = Environment
            .getExternalStorageDirectory() + "/DCIM";
    private String imageName;


    private CustomPopWindow customPopWindow;
    UserProviderService service;

    private boolean loadError = false;
    public ProgressBar mProgressBar;
    private PageStateManager mPageManager;
    private int bannerIndex, from_type;
    private BasePayPopWindow mPayPopWindow;
    private SignInPopWindow signInPopWindow = null;//签到


    public static void start(Context context, String title, String url, int bannerIndex, int from_type) {
        Intent intent = new Intent(context, BaseWebViewActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_FROM_TYPE, bannerIndex);
        intent.putExtra(EXTRA_URL_BANNER_INDEX, from_type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void start(Context context, String title, String url, boolean showBar, int bannerIndex, int from_type) {
        Intent intent = new Intent(context, BaseWebViewActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_SHOW_BAR, showBar);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_FROM_TYPE, bannerIndex);
        intent.putExtra(EXTRA_URL_BANNER_INDEX, from_type);
        context.startActivity(intent);
    }

    public static void start(Context context, String title, String url, boolean showBar, String color, int bannerIndex, int from_type) {
        Intent intent = new Intent(context, BaseWebViewActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_SHOW_BAR, showBar);
        intent.putExtra(EXTRA_TITLE_COLOR, color);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_FROM_TYPE, bannerIndex);
        intent.putExtra(EXTRA_URL_BANNER_INDEX, from_type);
        context.startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        service = ARouter.getInstance().navigation(UserProviderService.class);

        Intent intent = getIntent();
        bannerIndex = intent.getIntExtra(EXTRA_FROM_TYPE, 0);
        from_type = intent.getIntExtra(EXTRA_URL_BANNER_INDEX, 0);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String url = Utils.performUrl(intent.getStringExtra(EXTRA_URL));
        //增加统一参数 后台配置的url 设置默认0
        url += (url.contains("?") ? "&" : "?") + EXTRA_FROM_TYPE + "=" + from_type + "&" + EXTRA_URL_BANNER_INDEX + "=" + bannerIndex;
        FrameLayout container = findViewById(R.id.container);
        SoftHideKeyBoardUtil.assistActivity(this);

        String color = intent.getStringExtra(EXTRA_TITLE_COLOR);
        boolean showTitle = intent.getBooleanExtra(EXTRA_SHOW_BAR, true);
        String[] split = url.substring(url.indexOf("?") + 1).split("&");  //  [__app_name=peony] [webviewhead=true] [topcolor=black]
        for (String param : split) {
            if (!TextUtils.isEmpty(param) && param.contains("=")) {
                String[] split1 = param.split("=");
                if (split1.length == 2) {
                    if (TextUtils.equals(split1[0], "webviewhead")) {  // webviewhead=true
                        showTitle = !TextUtils.equals(split1[1], "true");
                    } else if (TextUtils.equals(split1[0], "topcolor")) { // topcolor=black
                        color = split1[1];
                    }
                }
            }
        }

        if (!showTitle) {
            RelativeLayout rlToolBar = findViewById(R.id.base_tool_bar);
            rlToolBar.setVisibility(View.GONE);
            StatusBarUtil.fullScreen(this);
        } else {
            new ToolBarBuilder().showBack(true)
                    .setTitle(title)
                    .build();
        }
        //黑色
        if (TextUtils.equals(color, "black")) {
            ImmersionBar.with(this)
                    .statusBarDarkFont(true, 0.2f)
                    .navigationBarDarkIcon(true, 0.2f)
                    .statusBarColor(R.color.transparent)
                    .init();
        }

        mProgressBar = findViewById(R.id.pb);
        mPageManager = PageStateManager.initWhenUse(this, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                loadError = false;
                mWebView.loadUrl(mWebView.getUrl());
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                finish();
            }
        });
        if (mWebView == null) mWebView = initWebView();
        container.addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setWebChromeClient(new WebChromeClient() {
                                        @Override
                                        public void onProgressChanged(WebView webView, int i) {
                                            super.onProgressChanged(webView, i);
                                            mProgressBar.setProgress(i);
                                            if (i == 100) {
                                                mProgressBar.setVisibility(View.GONE);
                                            }
                                        }

                                        // For Android >=3.0
                                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

                                            mCompositeDisposable.add(new RxPermissions(BaseWebViewActivity.this)
                                                    .request(Manifest.permission.RECORD_AUDIO
                                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                                                    .subscribe(aBoolean -> {
                                                        if (!aBoolean) {
                                                            PermissionUtil.showPermissionPopWebview(BaseWebViewActivity.this);
                                                        } else {
                                                            if (acceptType.equals("image/*")) {
                                                                if (mUploadMessage != null) {
                                                                    mUploadMessage.onReceiveValue(null);
                                                                    return;
                                                                }
                                                                mUploadMessage = uploadMsg;
                                                                selectImage();
                                                            } else {
                                                                onReceiveValue();
                                                            }
                                                        }
                                                    }));


                                        }

                                        // For Android < 3.0
                                        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                                            openFileChooser(uploadMsg, "image/*");
                                        }

                                        // For Android  >= 4.1.1
                                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                                            openFileChooser(uploadMsg, acceptType);
                                        }

                                        // For Android  >= 5.0
                                        @Override
                                        @SuppressLint("NewApi")
                                        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                                            mCompositeDisposable.add(new RxPermissions(BaseWebViewActivity.this)
                                                    .request(Manifest.permission.RECORD_AUDIO
                                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                                                    .subscribe(aBoolean -> {
                                                        if (!aBoolean) {
                                                            PermissionUtil.showPermissionPopWebview(BaseWebViewActivity.this);
                                                        } else {

                                                            if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                                                                    && fileChooserParams.getAcceptTypes().length > 0 && fileChooserParams.getAcceptTypes()[0].equals("image/*")) {
                                                                if (mUploadMessageArray != null) {
                                                                    mUploadMessageArray.onReceiveValue(null);
                                                                }
                                                                mUploadMessageArray = filePathCallback;
                                                                selectImage();
                                                            } else if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                                                                    && fileChooserParams.getAcceptTypes().length > 0 && fileChooserParams.getAcceptTypes()[0].equals("video/*")) {
                                                                if (mUploadMessageArray != null) {
                                                                    mUploadMessageArray.onReceiveValue(null);
                                                                }
                                                                mUploadMessageArray = filePathCallback;
                                                                recordVideo();
                                                            } else if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                                                                    && fileChooserParams.getAcceptTypes().length > 0 && fileChooserParams.getAcceptTypes()[0].equals("*/*")) {
                                                                if (mUploadMessageArray != null) {
                                                                    mUploadMessageArray.onReceiveValue(null);
                                                                }
                                                                mUploadMessageArray = filePathCallback;
                                                                openFileInput();
                                                            } else {
                                                                onReceiveValue();
                                                            }


                                                        }
                                                    }));

                                            return true;
                                        }

                                        @Override
                                        public void onReceivedTitle(WebView webView, String s) {
                                            super.onReceivedTitle(webView, s);
                                            if (TextUtils.isEmpty(title))
                                                ((TextView) findViewById(R.id.toolbar_title)).setText(s);
                                        }

                                    }
        );
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.setVisibility(View.GONE);
                mPageManager.showError(null);
                loadError = true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 兼容https
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!loadError && mPageManager != null && mWebView != null) {
                    mPageManager.showContent();
                    mWebView.setVisibility(View.VISIBLE);
                }
            }
        });
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
        LogUtil.e("=============", url);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (AppUtils.isAppDebug()) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        initRxBus();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_webview;
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                if (mPayPopWindow != null && mPayPopWindow.isShowing()) {
                                    mWebView.reload();
                                }
                            }
                        }
                ));
    }


    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
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
        ImmersionBar.destroy(this, null);
        super.onDestroy();
    }


    private X5WebView initWebView() {
        X5WebView webView = new X5WebView(this, null);
        WebSettings settings = webView.getSettings();
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
        webView.addJavascriptInterface(new AndroidtoJs(), "android");

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        return webView;
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

        /**
         * 分享
         */
        @JavascriptInterface
        public void openShare(int type, String url, String title, String content) {
            ShareHelper.get().openShare(BaseWebViewActivity.this, url, title, content, type);
        }

        /**
         * 分享
         */
        @JavascriptInterface
        public void imageShare(int type, String url) {
            ShareHelper.get().ShareImgToWX(BaseWebViewActivity.this, url, type);
        }


        @JavascriptInterface
        public void openPage(String link) {
            CommonUtil.performLink(mContext, new ConfigInfo.MineInfo(link), 0, 0);
        }

        /**
         * 跳转webview
         */
        @JavascriptInterface
        public void openWebView(String url, int is_topbar, String topbar_color) {
            start(BaseWebViewActivity.this, "", url, is_topbar != 0, topbar_color, 0, 0);
        }

        /**
         * 跳转语音房直播间
         */
        @JavascriptInterface
        public void toParty(String roomId, int partyId) {
            if (!ClickUtil.canOperate()) {
                return;
            }
            int id = MMKVUtils.getInstance().decodeInt(Constants.PARTY_ID);
            String yunId = MMKVUtils.getInstance().decodeString(Constants.PARAM_YUN_ROOM_ID);
            if (!TextUtils.isEmpty(yunId) && partyId != id) {
                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                    BaseMusicHelper.get().getPartyService().releaseAudience();
                }
                NIMClient.getService(ChatRoomService.class).exitChatRoom(yunId);
            }
            if (partyId == id) {
                finish();
                return;
            }
            ARouterUtils.joinRoom(roomId, partyId);
        }

        /**
         * 跳转资料页面
         */
        @JavascriptInterface
        public void toPersonInfo(int user_id) {
            if (service != null && service.getUserId() == user_id) {
                ARouterUtils.toMineDetailActivity("");
            } else {
                ARouterUtils.toMineDetailActivity(String.valueOf(user_id));
            }
        }

        /**
         * 跳转动态页面
         */
        @JavascriptInterface
        public void myBlogList() {
            ARouterUtils.toTrendActivity("", "");
        }

        /**
         * 充值
         * id:支付类型id
         */
        @JavascriptInterface
        public void openPay(int id, String rmb, String coin) {
            RechargeDto dto = new RechargeDto(id, rmb, coin);
            ARouterUtils.toChargePayActivity(dto, 0);

           /* runOnUiThread(() -> {
                if (mPayPopWindow == null) {
                    mPayPopWindow = new BasePayPopWindow(mActivity);
                }
                mPayPopWindow.setTypeId(id);
                mPayPopWindow.showPopupWindow();
            });*/
        }

        /**
         * 跳转充值列表
         */
        @JavascriptInterface
        public void toNativeRecharge() {
            ARouterUtils.toRechargeActivity();
        }

        /**
         * 跳转隐私设置页面
         */
        @JavascriptInterface
        public void openPrivacySetting() {
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_PRIVACY_SETTING);
        }

        /**
         * 跳转到反馈
         */
        @JavascriptInterface
        public void activityFeedback() {
            ARouterUtils.toReportActivity(-1, -1, 3);
        }

        /**
         * 跳转创建家族
         */
        @JavascriptInterface
        public void agreeOnFamily() {
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_CREATE_FAMILY);
            finish();
        }

        /**
         * 显示加载
         */
        @JavascriptInterface
        public void showDialog() {
            if (!isDestroyed())
                showLoadingDialog();
        }

        /**
         * 隐藏
         */
        @JavascriptInterface
        public void hideDialog() {
            if (!isDestroyed())
                hideLoadingDialog();
        }

        @JavascriptInterface
        public void closeWebView() {
            finish();
        }

        @JavascriptInterface
        public void openSignIn(String signInJson) {
            showSignInDialog(signInJson);
        }

        @JavascriptInterface
        public String getVersionName() {
            return AppUtils.getAppVersionName();
        }

        @JavascriptInterface
        public int getVersionCode() {
            return AppUtils.getAppVersionCode();
        }
    }

    /**
     * 签到
     */
    public void showSignInDialog(String signInJsonStr) {
        runOnUiThread(() -> {
            if (signInPopWindow == null) {
                signInPopWindow = new SignInPopWindow(this);
                signInPopWindow.setSignInListener((popup, bean) -> {
                    if (popup != null) popup.dismiss();
                    if (bean != null) new SignSucessPopWindow(mContext).showPop(bean);
                    if (bean != null && mWebView != null) mWebView.loadUrl("javascript:signInSuccess()");
                });
            }
            signInPopWindow.setData(signInJsonStr);
            if (!signInPopWindow.isShowing()) {
                signInPopWindow.showPopupWindow();
            }
        });
    }

    public void showTipPop(Activity activity) {
        runOnUiThread(() -> {
            if (customPopWindow == null)
                customPopWindow = new CustomPopWindow(activity);
            customPopWindow.setContent("您当前在麦位上，需要下麦后才能进入其他派对哦");
            customPopWindow.setSingleButtong("我知道了", Utils.getColor(R.color.color_normal));
            customPopWindow.showPopupWindow();
        });
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILE_CHOOSER) {
            if (resultCode == RESULT_OK) {
            } else {
            }
        }
    }*/

    //客服h5调用发送图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            onReceiveValue();
            return;
        }
        switch (requestCode) {
            case REQ_CAMERA:
                File fileCamera = new File(PATH, imageName);
                handleFile(fileCamera);
                break;
            case REQ_CHOOSE:
                Uri uri = intent.getData();
                String absolutePath = PathUtils.getPath(this, uri);
                File fileAlbum = new File(absolutePath);
                handleFile(fileAlbum);
                break;
            case VIDEO_REQUEST:
                if (null == mUploadMessage && null == mUploadMessageArray) return;

                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                if (mUploadMessageArray != null) {
                    if (resultCode == RESULT_OK) {
                        mUploadMessageArray.onReceiveValue(new Uri[]{result});
                        mUploadMessageArray = null;
                    } else {
                        mUploadMessageArray.onReceiveValue(new Uri[]{});
                        mUploadMessageArray = null;
                    }

                } else if (mUploadMessage != null) {
                    if (resultCode == RESULT_OK) {
                        mUploadMessage.onReceiveValue(result);
                        mUploadMessage = null;
                    } else {
                        mUploadMessage.onReceiveValue(Uri.EMPTY);
                        mUploadMessage = null;
                    }

                }

        }
    }

    private void onReceiveValue() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mUploadMessageArray != null) {
                mUploadMessageArray.onReceiveValue(null);
                mUploadMessageArray = null;
            }
        } else {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
        }
    }

    private void handleFile(File file) {
        if (file.isFile()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null == mUploadMessageArray) {
                    return;
                }
                Uri uri = Uri.fromFile(file);
                Uri[] uriArray = new Uri[]{uri};
                mUploadMessageArray.onReceiveValue(uriArray);
                mUploadMessageArray = null;
            } else {
                if (null == mUploadMessage) {
                    return;
                }
                Uri uri = Uri.fromFile(file);
                mUploadMessage.onReceiveValue(uri);
                mUploadMessage = null;
            }
        } else {
            onReceiveValue();
        }

    }

    private void openFileInput() {
        imageName = String.valueOf(System.currentTimeMillis());
        String picturePath = PATH;
        File file = new File(picturePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri output = Uri.fromFile(file);
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("*/*");

        Intent chooserIntent = Intent.createChooser(galleryIntent, "选择操作");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
        startActivityForResult(chooserIntent, REQ_CHOOSE);
    }

    /**
     * 录像
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //限制时长
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        //开启摄像机
        startActivityForResult(intent, VIDEO_REQUEST);
    }

    private void selectImage() {
        String[] selectPicTypeStr = {"拍摄", "从相册中选择"};
        new AlertDialog.Builder(this)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onReceiveValue();
                    }
                })
                .setItems(selectPicTypeStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            // 相机拍摄，此处调用系统相机拍摄图片，开发者可根据实际情况选用系统相机还是自己在这之上封装一层从而符合APP风格
                            case 0:
                                openCamera();
                                break;
                            // 手机相册，此处调用系统相册选择图片，开发者可根据实际情况选用系统相册还是自己在这之上封装一层从而符合APP风格
                            case 1:
                                openAlbum();
                                break;
                            default:
                                break;
                        }

                    }
                })
                .show();
    }

    private void openAlbum() {
        if (!hasSDcard()) {
            return;
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
            startActivityForResult(intent, REQ_CHOOSE);
        }
    }

    private void openCamera() {
        if (!hasSDcard()) {
            return;
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageName = System.currentTimeMillis() + ".png";
            File file = new File(PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(PATH, imageName)));
            startActivityForResult(intent, REQ_CAMERA);
        }
    }

    private boolean hasSDcard() {
        boolean flag = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (!flag) {
            Toast.makeText(this, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
            onReceiveValue();
        }
        return flag;
    }


}
