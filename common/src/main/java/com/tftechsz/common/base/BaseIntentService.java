package com.tftechsz.common.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.webkit.WebView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.InitListener;
import com.faceunity.nama.ui.BeautyParameterModel;
import com.faceunity.nama.utils.PreferenceUtil;
import com.heytap.msp.push.HeytapPushManager;
import com.hjq.toast.ToastUtils;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.previewlibrary.ZoomMediaLoader;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.tftechsz.common.BuildConfig;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.NimCache;
import com.tftechsz.common.nim.NimSDKOptionConfig;
import com.tftechsz.common.nim.SessionHelper;
import com.tftechsz.common.nim.UserPreferences;
import com.tftechsz.common.player.controller.VideoViewManager;
import com.tftechsz.common.utils.ImageLoaderUtil;
import com.tftechsz.common.utils.Utils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import iknow.android.utils.BaseUtils;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;

/**
 * 包 名 : com.tftechsz.common.base
 * 描 述 : TODO
 */
public class BaseIntentService extends JobIntentService {

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Utils.logE("start-JobIntentService:" + Thread.currentThread().getName());
        PlatformConfig.setWeixin(Constants.WX_APP_ID, Constants.WX_APP_SC);
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, R.color.color_normal);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
        if (BuildConfig.IS_DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ToastUtils.init(BaseApplication.getInstance());
        com.blankj.utilcode.util.Utils.init(BaseApplication.getInstance());
        ARouter.init(BaseApplication.getInstance()); // 尽可能早，推荐在Application中初始化
        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(BaseApplication.getInstance());
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志z
                Utils.logE("注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Utils.logE("注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                //使用使用IjkPlayer解码
//                .setPlayerFactory(IjkPlayerFactory.create())
                //使用ExoPlayer解码
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                //使用MediaPlayer解码
                //.setPlayerFactory(AndroidMediaPlayerFactory.create())
                .build());
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
        UMConfigure.setProcessEvent(true);
        UMShareAPI.get(BaseApplication.getInstance());
        NimCache.setContext(BaseApplication.getInstance());
        SDKOptions sdkOptions = NimSDKOptionConfig.getSDKOptions(BaseApplication.getInstance());
        NIMClient.init(BaseApplication.getInstance(), getLoginInfo(), sdkOptions);
        if (NIMUtil.isMainProcess(BaseApplication.getInstance())) {
            // 监听的注册，必须在主进程中。
            HeytapPushManager.init(BaseApplication.getInstance(), true);
            com.huawei.hms.support.common.ActivityMgr.INST.init(BaseApplication.getInstance());
            SessionHelper.init();
            initUiKit();
            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
        }
        //闪验
        OneKeyLoginManager.getInstance().setDebug(true);
        initShanyanSDK(BaseApplication.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(Process.myPid() + "");
        }
        ZoomMediaLoader.getInstance().init(new ImageLoaderUtil());
        DownloadHelper.init(BaseApplication.getInstance());
        enableHttpResponseCache();
        PreferenceUtil.init(BaseApplication.getInstance());
        BeautyParameterModel.init();
        BaseUtils.init(BaseApplication.getInstance());
        Utils.logE("end-JobIntentService:" + Thread.currentThread().getName());
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


    private void initUiKit() {
        // 初始化
        NimUIKit.init(BaseApplication.getInstance());
        // 会话窗口的定制: 示例代码可详见demo源码中的SessionHelper类。
        // 1.注册自定义消息附件解析器（可选）
        // 2.注册各种扩展消息类型的显示ViewHolder（可选）
        // 3.设置会话中点击事件响应处理（一般需要）
//        SessionHelper.init();

    }


    private LoginInfo getLoginInfo() {
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        String account = String.valueOf(service.getUserId());
        String token = service.getToken();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            NimCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }


    /**
     * 闪验
     *
     * @param context
     */
    private void initShanyanSDK(Context context) {
        OneKeyLoginManager.getInstance().init(context, Constants.SANYAN_APP_ID, new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
            }
        });
    }

}
