package com.tftechsz.common.base;

import static com.tftechsz.common.utils.CommonUtil.getUmengAppKey;
import static com.tftechsz.common.utils.CommonUtil.getUmengChannel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.faceunity.nama.ui.BeautyParameterModel;
import com.faceunity.nama.utils.PreferenceUtil;
import com.heytap.msp.push.HeytapPushManager;
import com.hjq.toast.ToastUtils;
import com.hua.permissionmonitor.PermissionMonitor;
import com.netease.lava.api.Trace;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.NIMPushClient;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.previewlibrary.ZoomMediaLoader;
import com.tencent.mmkv.MMKV;
import com.tencent.tauth.Tencent;
import com.tftechsz.common.BuildConfig;
import com.tftechsz.common.Constants;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.NimCache;
import com.tftechsz.common.nim.NimSDKOptionConfig;
import com.tftechsz.common.nim.SessionHelper;
import com.tftechsz.common.nim.UserPreferences;
import com.tftechsz.common.player.controller.VideoViewManager;
import com.tftechsz.common.push.MyMixPushMessageHandler;
import com.tftechsz.common.push.MyPushContentProvider;
import com.tftechsz.common.utils.ImageLoaderUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.widget.MyToastStyle;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.loader.glide.GlideImageLoader;
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import iknow.android.utils.BaseUtils;
import io.reactivex.plugins.RxJavaPlugins;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;


/**
 * Description: <初始化应用程序><br>
 */
public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public final static int APP_STATUS_KILLED = 0; // 表示应用是被杀死后在启动的
    public final static int APP_STATUS_NORMAL = 1; // 表示应用时正常的启动流程
    public static int APP_STATUS = APP_STATUS_KILLED; // 记录App的启动状态
    private Tencent mTecentInstance;

//    static {
//        PlatformConfig.setWeixin(Constants.WX_APP_ID, Constants.WX_APP_SC);
//        //设置全局的Header构建器
//        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//            layout.setPrimaryColorsId(R.color.white, R.color.color_normal);//全局设置主题颜色
//            return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
//        });
//        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
//            //指定为经典Footer，默认是 BallPulseFooter
//            layout.setReboundDuration(0);
//            return new ClassicsFooter(context).setDrawableSize(20);
//        });
//    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

        if(BuildConfig.DEBUG) {
            PermissionMonitor.start(false);
        }
    }

    private static BaseApplication mApplication;

    public static BaseApplication getInstance() {
        return mApplication;
    }

    public Tencent getTencentInstance() {
        if (mTecentInstance == null) {
            mTecentInstance = Tencent.createInstance("tencent_id", this.getApplicationContext(), AppUtils.getAppPackageName() + ".fileProvider");
        }
        return mTecentInstance;
    }

    //bugly-io.reactivex.exceptions.UndeliverableException: The exception could not be delivered to the consumer because it has alr
    private void setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
            Trace.e("MyApplication", "MyApplication setRxJavaErrorHandler " + throwable.getMessage());
        });
    }

    private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this);
        registerActivityLifecycleCallbacks(this);
        mApplication = this;
        setRxJavaErrorHandler();
        if (processName == null) {
            return;
        }
        if (AppUtils.isAppDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this);
        NimCache.setContext(this);
        NIMClient.init(this, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Field field = ClassLoader.getSystemClassLoader()
                            .loadClass("de.robv.android.xposed.XposedBridge")
                            .getDeclaredField("disableHooks");
                    field.setAccessible(true);
                    field.set(null, Boolean.TRUE);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                enableHttpResponseCache();
            }
        }).start();

        if (NIMUtil.isMainProcess(this)) {
            UMConfigure.setLogEnabled(BuildConfig.DEBUG);
            UMConfigure.preInit(this, getUmengAppKey(), getUmengChannel());
            // 监听的注册，必须在主进程中。
            HeytapPushManager.init(this, true);
            com.huawei.hms.support.common.ActivityMgr.INST.init(this);
            initUiKit();
            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
            NIMPushClient.registerMixPushMessageHandler(new MyMixPushMessageHandler());
            ToastUtils.init(mApplication, new MyToastStyle());
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
            VideoViewManager.setConfig(VideoViewConfig.newBuilder().setPlayerFactory(IjkPlayerFactory.create()).build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WebView.setDataDirectorySuffix(Process.myPid() + "");
            }
            ZoomMediaLoader.getInstance().init(new ImageLoaderUtil());
            DownloadHelper.init(this);
            PreferenceUtil.init(this);
            BeautyParameterModel.init();
            BaseUtils.init(this);
            MMKV.initialize(this);
            Mojito.initialize(GlideImageLoader.Companion.with(this), new SketchImageLoadFactory());
            if (TextUtils.isEmpty(MMKVUtils.getInstance().decodeString(Interfaces.SP_OAID))) {
                com.netease.nis.sdkwrapper.Utils.rL(new Object[]{null, BaseApplication.getInstance(),
                        true, (IIdentifierListener) (b, idSupplier) -> {
                    String oaid = idSupplier.getOAID();
                    MMKVUtils.getInstance().encode(Interfaces.SP_OAID, oaid);
                }, 28, 1606976968500L});
            }
//            notifyChannel(this);
        }
//        initBytedance();

        // 尽可能早，推荐在Application中初始化
        //获取消息推送代理示例
        /*PushAgent mPushAgent = PushAgent.getInstance(this);
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
        });*/
       /* if (!FFmpeg.getInstance(this).isSupported()) {
            Log.e("ZApplication","Android cup arch not supported!");
        }*/
//        setMyInstrumentation();
    }


    /**
     * 字节跳动推广接入
     */
    private void initBytedance() {

        /* 初始化开始 */
//        final InitConfig config = new InitConfig("__TBD__", AnalyticsConfig.getChannel(this)); // appid和渠道，appid须保证与广告后台申请记录一致，渠道可自定义，如有多个马甲包建议设置渠道号唯一标识一个马甲包。
//        //上报域名可根据业务情况自己设置上报域名，国内版本只支持上报到DEFAULT，海外GDRP版本只支持SINGAPORE、AMERICA
//        /* 国内: DEFAULT */
//        config.setUriConfig(UriConstants.DEFAULT);
////        config.setLogger ((msg, t) -> Log._d_ (TAG, msg, t)); // 是否在控制台输出日志，可用于观察用户行为日志上报情况，建议仅在调试时使用
////        config.setEnablePlay(true); // 是否开启游戏模式，游戏APP建议设置为 true
//        config.setAbEnable(true); // 是否开启A/B Test功能
//        config.setAutoStart(true);
//        AppLog.init(this, config);
//        /* 初始化结束 */
//
//              /* 自定义 “用户公共属性”（可选，初始化后调用, key相同会覆盖）
//              关于自定义 “用户公共属性” 请注意：1. 上报机制是随着每一次日志发送进行提交，默认的日志发送频率是1分钟，所以如果在一分钟内连续修改自定义用户公共属性，，按照日志发送前的最后一次修改为准， 2. 不推荐高频次修改，如每秒修改一次 */
//        Map headerMap = new HashMap();
//        headerMap.put("level", 8);
//        headerMap.put("gender", "female");
//        AppLog.setHeaderInfo((HashMap) headerMap);

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
        NimUIKit.init(this);
        // 会话窗口的定制: 示例代码可详见demo源码中的SessionHelper类。
        // 1.注册自定义消息附件解析器（可选）
        // 2.注册各种扩展消息类型的显示ViewHolder（可选）
        // 3.设置会话中点击事件响应处理（一般需要）
        SessionHelper.init();
        NimUIKit.setCustomPushContentProvider(new MyPushContentProvider());

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


    int i = 0;
    boolean isForeground = false;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        isForeground = true;
        if (i == 0) {
            LogUtil.e("前后台切换", "onActivityStarted: 切换到前台");
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_APP_STARTED));
        }
        i++;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        LogUtil.i("BaseApplication", "onActivityResumed: 位置" + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        i--;
        if (i == 0) {
            isForeground = false;
            LogUtil.e("前后台切换", "onActivityStarted: 切换到后台");
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_APP_STOPPED));
        } else {
            isForeground = true;
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    //让自己的应用不会随系统字体大小而变化,始终保持一致
   /* @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }*/

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        BaseApplication app = (BaseApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .build();
    }


    public static class MyInstrumentation extends Instrumentation {
        @Override
        public boolean onException(Object obj, Throwable e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                if (e.toString().contains("DeadSystemException")) {
                    return true;
                }
                return super.onException(obj, e);
            }
            return super.onException(obj, e);

        }
    }

    private void setMyInstrumentation() {
        try {
            MyInstrumentation ins = new MyInstrumentation();
            Class cls = Class.forName("android.app.ActivityThread"); // ActivityThread被隐藏了，所以通过这种方式获得class对象
            Method mthd = cls.getDeclaredMethod("currentActivityThread", (Class[]) null); // 获取当前ActivityThread对象引用
            Object currentAT = mthd.invoke(null, (Object[]) null);
            Field mInstrumentation = currentAT.getClass().getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);//设置private变量为可读取
            mInstrumentation.set(currentAT, ins); // 修改ActivityThread.mInstrumentation值
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
