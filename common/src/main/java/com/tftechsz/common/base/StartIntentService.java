package com.tftechsz.common.base;

import static com.tftechsz.common.utils.CommonUtil.getUmengAppKey;
import static com.tftechsz.common.utils.CommonUtil.getUmengChannel;
import static com.tftechsz.common.utils.CommonUtil.getUmengPushSecret;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.webkit.WebView;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.previewlibrary.ZoomMediaLoader;
import com.tftechsz.common.utils.ImageLoaderUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.UMShareAPI;

import java.io.File;

public class StartIntentService extends IntentService {
    private static final String ACTION_INIT = "StartIntentService";

    public StartIntentService() {
        super("StartIntentService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, StartIntentService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT.equals(action)) {
                initApplication();
            }
        }
    }

    /**
     * 子线程进行初始化SDK操作
     **/
    private void initApplication() {
        LogUtil.e("==============","=================");
        UMConfigure.init(this, getUmengAppKey(), getUmengChannel(), UMConfigure.DEVICE_TYPE_PHONE, getUmengPushSecret());
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
        UMConfigure.setProcessEvent(true);
        UMShareAPI.get(this);
        //闪验
        OneKeyLoginManager.getInstance().setDebug(true);
        initShanyanSDK(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(Process.myPid() + "");
        }
        ZoomMediaLoader.getInstance().init(new ImageLoaderUtil());
        DownloadHelper.init(BaseApplication.getInstance());
        enableHttpResponseCache();
    }


    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024;// 10M
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception e) {
        }
    }

    /**
     * 闪验
     *
     * @param context
     */
    private void initShanyanSDK(Context context) {
//        OneKeyLoginManager.getInstance().init(context, Constants.SANYAN_APP_ID, new InitListener() {
//            @Override
//            public void getInitStatus(int code, String result) {
//            }
//        });
    }


}
