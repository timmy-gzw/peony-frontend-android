package com.tftechsz.common.utils;

import static com.tftechsz.common.constant.Interfaces.LINK_WEBVIEW;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.ValueOf;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.constant.OpenAuthListener;
import com.tftechsz.common.entity.VideoInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.nim.ChatSoundPlayer;
import com.tftechsz.common.player.view.MyVideoViewActivity;
import com.tftechsz.common.widget.CustomFilter;
import com.tftechsz.common.widget.pop.SVGAPlayerPop;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     author:
 *                                      ___           ___           ___         ___
 *         _____                       /  /\         /__/\         /__/|       /  /\
 *        /  /::\                     /  /::\        \  \:\       |  |:|      /  /:/
 *       /  /:/\:\    ___     ___    /  /:/\:\        \  \:\      |  |:|     /__/::\
 *      /  /:/~/::\  /__/\   /  /\  /  /:/~/::\   _____\__\:\   __|  |:|     \__\/\:\
 *     /__/:/ /:/\:| \  \:\ /  /:/ /__/:/ /:/\:\ /__/::::::::\ /__/\_|:|____    \  \:\
 *     \  \:\/:/~/:/  \  \:\  /:/  \  \:\/:/__\/ \  \:\~~\~~\/ \  \:\/:::::/     \__\:\
 *      \  \::/ /:/    \  \:\/:/    \  \::/       \  \:\  ~~~   \  \::/~~~~      /  /:/
 *       \  \:\/:/      \  \::/      \  \:\        \  \:\        \  \:\         /__/:/
 *        \  \::/        \__\/        \  \:\        \  \:\        \  \:\        \__\/
 *         \__\/                       \__\/         \__\/         \__\/
 *     blog  : http://blankj.com
 *     time  : 16/12/08
 *     desc  : utils about initialization
 * </pre>
 */
public final class Utils {

    //一对一视频通话
    public static int ONE_TO_ONE_CALL = 0;

    //多人通话
    public static int GROUP_CALL = 1;
    private static final String PERMISSION_ACTIVITY_CLASS_NAME =
            "com.blankj.utilcode.util.PermissionUtils$PermissionActivity";

    private static final ActivityLifecycleImpl ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();
    public static final ExecutorService UTIL_POOL = Executors.newFixedThreadPool(3);
    private static final Handler UTIL_HANDLER = new Handler(Looper.getMainLooper());

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param context context
     */
    public static void init(final Context context) {
        if (context == null) {
            init(getApplicationByReflect());
            return;
        }
        init((Application) context.getApplicationContext());
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param app application
     */
    public static void init(final Application app) {
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
            sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } else {
            if (app != null && app.getClass() != sApplication.getClass()) {
                sApplication.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
                ACTIVITY_LIFECYCLE.mActivityList.clear();
                sApplication = app;
                sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
            }
        }
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            return className.equals(cpn.getClassName());
        }
        return false;
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

    static ActivityLifecycleImpl getActivityLifecycle() {
        return ACTIVITY_LIFECYCLE;
    }

    static LinkedList<Activity> getActivityList() {
        return ACTIVITY_LIFECYCLE.mActivityList;
    }

    public static Context getTopActivityOrApp() {
        if (isAppForeground()) {
            Activity topActivity = ACTIVITY_LIFECYCLE.getTopActivity();
            return topActivity == null ? Utils.getApp() : topActivity;
        } else {
            return Utils.getApp();
        }
    }

    static boolean isAppForeground() {
        ActivityManager am = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return false;
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (aInfo.processName.equals(Utils.getApp().getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    static <T> Task<T> doAsync(final Task<T> task) {
        UTIL_POOL.execute(task);
        return task;
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            Utils.UTIL_HANDLER.post(runnable);
        }
    }

    public static void runOnUiThreadDelayed(final Runnable runnable, long delayMillis) {
        Utils.UTIL_HANDLER.postDelayed(runnable, delayMillis);
    }

    public static Timeout setTimeout(Runnable runnable, long delayMillis) {
        UTIL_HANDLER.postDelayed(runnable, delayMillis);
        return new Timeout(UTIL_HANDLER, runnable);
    }


    public static Timeout setTimeout(Handler handler, Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
        return new Timeout(handler, runnable);
    }

    static class Timeout {
        private final Handler handler;
        private final Runnable runnable;

        public Timeout(Handler handler, Runnable runnable) {
            this.handler = handler;
            this.runnable = runnable;
        }

        public void cancel() {
            if (handler != null)
                handler.removeCallbacks(runnable);
        }
    }


    public static void removeHandler() {
        Utils.UTIL_HANDLER.removeCallbacksAndMessages(null);

    }


    static String getCurrentProcessName() {
        String name = getCurrentProcessNameByFile();
        if (!TextUtils.isEmpty(name)) return name;
        name = getCurrentProcessNameByAms();
        if (!TextUtils.isEmpty(name)) return name;
        name = getCurrentProcessNameByReflect();
        return name;
    }

    static void fixSoftInputLeaks(final Window window) {
        InputMethodManager imm =
                (InputMethodManager) Utils.getApp().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        String[] leakViews = new String[]{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"};
        for (String leakView : leakViews) {
            try {
                Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                if (!leakViewField.isAccessible()) {
                    leakViewField.setAccessible(true);
                }
                Object obj = leakViewField.get(imm);
                if (!(obj instanceof View)) continue;
                View view = (View) obj;
                if (view.getRootView() == window.getDecorView().getRootView()) {
                    leakViewField.set(imm, null);
                }
            } catch (Throwable ignore) {/**/}
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // private method
    ///////////////////////////////////////////////////////////////////////////

    private static String getCurrentProcessNameByFile() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentProcessNameByAms() {
        ActivityManager am = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return "";
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) return "";
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName;
                }
            }
        }
        return "";
    }

    private static String getCurrentProcessNameByReflect() {
        String processName = "";
        try {
            Application app = Utils.getApp();
            Field loadedApkField = app.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(app);

            if (loadedApk != null) {
                Field activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
                activityThreadField.setAccessible(true);
                Object activityThread = activityThreadField.get(loadedApk);

                if (activityThread != null) {
                    Method getProcessName = activityThread.getClass().getDeclaredMethod("getProcessName");
                    processName = (String) getProcessName.invoke(activityThread);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processName;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    /**
     * Set animators enabled.
     */
    private static void setAnimatorsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
            return;
        }
        try {
            //noinspection JavaReflectionMemberAccess
            Field sDurationScaleField = ValueAnimator.class.getDeclaredField("sDurationScale");
            sDurationScaleField.setAccessible(true);
            float sDurationScale = (Float) sDurationScaleField.get(null);
            if (sDurationScale == 0f) {
                sDurationScaleField.set(null, 1f);
                Log.i("Utils", "setAnimatorsEnabled: Animators are enabled now!");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void logE(Object s) {
        if (AppUtils.isAppDebug()) {
            Log.e("Utils.logE", String.valueOf(s));
        }
    }

    //設置焦點
    public static void setFocus(View view) {
        setFocus(view, true);
    }

    //設置焦點
    public static void setFocus(View view, boolean isFocus) {
        view.setFocusable(isFocus);
        view.setFocusableInTouchMode(isFocus);
        if (isFocus) {
            view.requestFocus();
            view.requestFocusFromTouch();
            if (view instanceof EditText) {
                EditText edt = (EditText) view;
                edt.setSelection(getText(edt).length());
            }
        }
    }

    public static void toast(String msg) {
        toast(msg, true);
    }

    public static void toast(int msg) {
        toast(getString(msg), true);
    }

    public static void toast(Object msg, boolean isCenter) {
        if (ObjectUtils.isEmpty(msg)) {
            return;
        }
        if (isCenter) {
            com.hjq.toast.ToastUtils.show(String.valueOf(msg));
        } else {
            ToastUtils.showShort(String.valueOf(msg));
        }
    }

    public static Bitmap base64ToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static boolean checkNull(EditText editText, String errorMsg) {
        if (TextUtils.isEmpty(getText(editText))) {
            toast(errorMsg);
            setFocus(editText);
            return true;
        }
        return false;
    }

    public static void hideSoftKeyBoard(Activity activity) {
        UTIL_HANDLER.postDelayed(() -> {
            View localView = activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (localView != null && imm != null) {
                imm.hideSoftInputFromWindow(localView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 50);
    }

    /**
     * 全屏播放搭讪成功动画以及播放音效
     */
    public static void playAccostAnimationAndSound(String giftName, String giftAnimationOfSVGA) {
        if (TextUtils.isEmpty(giftAnimationOfSVGA)) return;
        SVGAPlayerPop svgaPop = new SVGAPlayerPop(BaseApplication.getInstance());
        svgaPop.addSvga(giftName, giftAnimationOfSVGA);
        //播放音频
        ChatSoundPlayer.instance().play(ChatSoundPlayer.RingerTypeEnum.ACCOST);
    }

    public static void playAccostAnimation(Context context, View v1, View v2) {

        runOnUiThread(() -> {
            Utils.logE("播放动画 ");
            Animation aniDate = AnimationUtil.startScaleOutAnim(context, v1);
            aniDate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (v1 != null) {
                        v1.setVisibility(View.GONE);
                        v1.clearAnimation();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            Animation animation2 = AnimationUtil.startScaleInAnim(context, v2);
            animation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (v2 != null) {
                        v2.setVisibility(View.VISIBLE);
                        v2.clearAnimation();
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            //播放音频
            ChatSoundPlayer.instance().play(ChatSoundPlayer.RingerTypeEnum.ACCOST);
//            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.hit_up);
//            mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.release());
//            mediaPlayer.start();

        });


    }

    public static void playLottieAnimation(LottieAnimationView lottieAnimationView) {
        if (lottieAnimationView != null) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            lottieAnimationView.postDelayed(() -> lottieAnimationView.setVisibility(View.GONE), lottieAnimationView.getDuration());
        }
    }

    public static boolean fileIsVideo(String mimeType) {
        return mimeType.equals(PictureMimeType.ofMP4())
                || mimeType.equals(PictureMimeType.of3GP())
                || mimeType.equals(PictureMimeType.ofAVI())
                || mimeType.equals(PictureMimeType.ofMPEG())
                ;

    }

    public static void playVideo(Context context, String videoPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(UriUtils.file2Uri(new File(videoPath)), "video/*");
        context.startActivity(intent);
    }

    public static int[] getVideoWidthHeight(String realPath) {
        MediaMetadataRetriever retriever = null;
        FileInputStream inputStream = null;
        try {
            retriever = new MediaMetadataRetriever();
            inputStream = new FileInputStream(realPath);
            retriever.setDataSource(inputStream.getFD());
            int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); //宽
            int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); //高
            int rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));//视频的方向角度
            if (90 == rotation || 270 == rotation) {
                return new int[]{height, width};
            } else {
                return new int[]{width, height};
            }
        } catch (Exception e) {
            logE("Exception: " + e.getMessage());
        } finally {
            if (retriever != null) {
                retriever.release();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return new int[]{800, 800};
    }

    /**
     * 将View从父控件中移除
     */
    public static void removeViewFormParent(View v) {
        if (v == null) return;
        ViewParent parent = v.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(v);
        } else if (parent instanceof RelativeLayout) {
            ((RelativeLayout) parent).removeView(v);
        } else if (parent instanceof LinearLayout) {
            ((LinearLayout) parent).removeView(v);
        }
    }

    public static ViewGroup.LayoutParams filterVideoWH(ViewGroup.LayoutParams lp,
                                                       int max_screen_width, int max_screen_height, int video_width, int video_height) {
        if (video_width == video_height) { //相等, 正方形视频
            lp.width = max_screen_width;
            lp.height = max_screen_width;
        } else if (video_width > video_height) { //横屏视频
            lp.width = max_screen_width;
            lp.height = (int) (video_height / (video_width * 1f / lp.width));
        } else { //竖屏视频
            lp.height = max_screen_height;
            lp.width = (int) (video_width / (video_height * 1f / max_screen_height));
        }
        return lp;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getOldYearDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.YEAR, date.get(Calendar.YEAR) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert endDate != null;
        return dft.format(endDate);
    }

    public static String[] splitBirthday(String text) {
        if (text.contains("-")) {
            return text.split("-");
        } else if (text.contains("/")) {
            return text.split("/");
        } else if (text.contains("_")) {
            return text.split("_");
        }
        return new String[]{};
    }

    public static void finishAfterTransition(Activity activity) {
        ActivityCompat.finishAfterTransition(activity);
    }

    public static void addTransitionBar(Activity activity, List<Pair<View, String>> pairs) {
        Pair<View, String> pairStatusBar = new Pair<>(activity.findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
        if (pairStatusBar.first != null) {
            pairs.add(pairStatusBar);
        }
        Pair<View, String> pairNavigationBar = new Pair<>(activity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
        if (pairNavigationBar.first != null) {
            pairs.add(pairNavigationBar);
        }
    }

    public static String getString(int resId) {
        return BaseApplication.getInstance().getResources().getString(resId);
    }

    public static Animation getTopScaleAnimation(float v, float v1, boolean isSHow) {
        ScaleAnimation animation;
        if (isSHow) {
            animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, v, Animation.RELATIVE_TO_SELF, v1);
        } else {
            animation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, v, Animation.RELATIVE_TO_SELF, v1);
        }
        animation.setDuration(400);
        animation.setInterpolator(isSHow ? new AnticipateOvershootInterpolator() : new OvershootInterpolator());
        return animation;
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    //图片翻转
    public static Bitmap convert(Bitmap a) {
        int w = a.getWidth();
        int h = a.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        Matrix m = new Matrix();
        // m.postScale(1, -1);   //镜像垂直翻转
        m.postScale(-1, 1);   //镜像水平翻转
        Bitmap new2 = Bitmap.createBitmap(a, 0, 0, w, h, m, true);
        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()), new Rect(0, 0, w, h), null);
        return newb;
    }

    public static String getUmId() {
        String umId = SPUtils.getInstance().getString(Interfaces.SP_UMENG_ID);
        if (!TextUtils.isEmpty(umId)) {
            return umId;
        }
        umId = DeviceConfig.getDeviceIdForGeneral(BaseApplication.getInstance());
        if (TextUtils.isEmpty(umId) || umId.startsWith("00000") || umId.equals("0")) {
            umId = MMKVUtils.getInstance().decodeString(Interfaces.SP_OAID);
            if (TextUtils.isEmpty(umId)) {
                umId = com.tftechsz.common.utils.AppUtils.getDeviceId();
            } else {
                com.tftechsz.common.utils.AppUtils.saveDeviceIdType("oaid", umId);
            }
        } else {
            String deviceIdType = DeviceConfig.getDeviceIdType();
            com.tftechsz.common.utils.AppUtils.saveDeviceIdType("u_" + deviceIdType, umId);
        }

        SPUtils.getInstance().put(Interfaces.SP_UMENG_ID, umId);
        return umId;
    }

    /**
     * 获取device_id的来源 umeng id: u_xx
     */
    public static String getDeviceIdType() {
        return SPUtils.getInstance().getString(Interfaces.SP_DEVICE_ID_TYPE, "");
    }

    public static boolean checkNoTell(String phone) {
        if (TextUtils.isEmpty(phone)) {
            toast("请输入手机号");
            return true;
        }
        if (phone.length() != 11 || !(phone.startsWith("298") || phone.startsWith("1"))) {
            toast("请输入正确的手机号");
            return true;
        }
        return false;
    }

    public static boolean checkSMSCode(String code) {
        if (TextUtils.isEmpty(code)) {
            toast("请输入验证码");
            return true;
        }
        if (code.length() != 4) {
            toast("请输入正确的验证码");
            return true;
        }
        return false;
    }

    public static String performUrl(String url) { // http://m.dev.peony.taifangsz.com.com/policy.html?v=2021011310002&__app_name={app_name}
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        return url.replace(Constants.WEB_PARAMS_APP_NAME, Constants.APP_NAME)
                .replace(Constants.WEB_PARAMS_APP_VERSION, AppUtils.getAppVersionName())
                .replace(Constants.WEB_PARAMS_APP_SYSTEM, "android")
                .replace(LINK_WEBVIEW, "");
    }

    /**
     * 设置EditText是否可编辑
     *
     * @param editText
     * @param mode
     */
    public static void setEdittextEnable(EditText editText, boolean mode) {
        editText.setFocusable(mode);
        editText.setFocusableInTouchMode(mode);
        editText.setLongClickable(mode);
        editText.setInputType(mode ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
    }

    public static void putPayType(int type) {
        MMKVUtils.getInstance().encode(Interfaces.SP_IS_FAMILY_IN, type);
    }

    public static boolean isPayTypeInFamily() {
        return isPayTypeInFamily(MMKVUtils.getInstance().decodeInt(Interfaces.SP_IS_FAMILY_IN));
    }

    public static boolean isPayTypeInFamily(int form_type) {
        return form_type == 1;
    }

    public static String getPayTypeFrom() {
        return getPayTypeFrom(MMKVUtils.getInstance().decodeInt(Interfaces.SP_IS_FAMILY_IN));
    }

    public static String getPayTypeFrom(int type) {
        if (type == 1) {
            return "family";
        } else if (type == 2) {
            return "party";
        }
        return "";
    }

    public static void setTextOrVisibility(TextView textView, String msg) {
        if (TextUtils.isEmpty(msg)) {
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setText(msg);
        textView.setVisibility(View.VISIBLE);
    }

    public static boolean regexEmail(ConfigInfo configInfo, String number) {
        if (configInfo != null && configInfo.sys != null && configInfo.sys.regex != null) {
            return RegexUtils.isMatch(configInfo.sys.regex.email, number);
        }
        return RegexUtils.isMatch("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", number);
    }

    public static boolean regexTell(ConfigInfo configInfo, String number) {
        if (configInfo != null && configInfo.sys != null && configInfo.sys.regex != null) {
            return RegexUtils.isMatch(configInfo.sys.regex.tell, number);
        }
        return RegexUtils.isMatch("^((13[0-9])|(14[579])|(15[0-35-9])|(16[2567])|(17[0-35-8])|(18[0-9])|(19[0-35-9]))\\d{8}$", number);
    }

    public static boolean regexIdCard(ConfigInfo configInfo, String number) {
        if (configInfo != null && configInfo.sys != null && configInfo.sys.regex != null) {
            return RegexUtils.isMatch(configInfo.sys.regex.id_card, number);
        }
        return RegexUtils.isMatch("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$", number);
    }

    public static boolean regexChineseName(ConfigInfo configInfo, String number) {
        if (configInfo != null && configInfo.sys != null && configInfo.sys.regex != null) {
            return RegexUtils.isMatch(configInfo.sys.regex.chinese, number);
        }
        return RegexUtils.isMatch("^[\\u4e00-\\u9fa5·]+$", number);
    }

    public static int filterTime(String voice_time) {
        String s = voice_time.replace("s", "").replace("S", "");
        if (RegexUtils.isMatch("\\d*$", s.trim())) {
            return Integer.parseInt(s.trim());
        }
        return -1;
    }

    /**
     * 设置当前窗口亮度
     *
     * @param brightness
     */
    public static void setWindowBrightness(Activity context, float brightness) {
        Window window = context.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }


    /**
     * 生成uri
     *
     * @param context
     * @param cameraFile
     * @return
     */
    public static Uri parUri(Context context, File cameraFile) {
        Uri imageUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            String authority = context.getPackageName() + ".fileProvider";
            imageUri = FileProvider.getUriForFile(context, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param context
     * @return 图片的uri
     */
    @Nullable
    public static Uri createImageUri(final Context context) {
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        }
        values.put(MediaStore.Images.Media.MIME_TYPE, PictureMimeType.MIME_TYPE_IMAGE);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM);
            }
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }

    public static CharSequence matchWechatNumber(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_') {
                sb.append(c);
            }
        }
        return sb;
    }


    public static void isOpenAuth(OpenAuthListener listener) {
        new CompositeDisposable().add(RetrofitManager.getInstance().createUserApi(PublicService.class)
                .checkModel(DeviceUtils.getModel())
                .compose(RxUtil.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> data) {
                        if (listener != null) {
                            listener.callback(data.getData());
                        }
                    }

                }));

    /*    UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null) {
            return service.getConfigInfo().sys.is_open_auth == 1;
        }
        return true;*/
    }

    public static String getLastTime(long time) {
        long h = time / 3600 % 24;
        long m = time / 60 % 60;
        long s = time % 60;
        return String.format("%s:%s:%s", h, m > 9 ? m : ("0" + m), s > 9 ? s : ("0" + s));
    }

    public static String getLastTime1(long time) {
        long m = time / 60000 % 60;
        long s = time / 1000 % 60;
        return String.format("%s:%s", m > 9 ? m : ("0" + m), s > 9 ? s : ("0" + s));
    }

    public static String getChannel() {
        return AnalyticsConfig.getChannel(BaseApplication.getInstance());
//        return "3";
    }

    public static int numberFormat(String cate) {
        int i = 0;
        try {
            i = Integer.parseInt(cate);
        } catch (NumberFormatException ignored) {

        }
        return i;
    }

    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url)) return "";
        int lastSep = url.lastIndexOf(File.separator);
        return lastSep == -1 ? url : url.substring(lastSep + 1);
    }

    static class ActivityLifecycleImpl implements ActivityLifecycleCallbacks {

        final LinkedList<Activity> mActivityList = new LinkedList<>();
        final Map<Object, OnAppStatusChangedListener> mStatusListenerMap = new HashMap<>();
        final Map<Activity, Set<OnActivityDestroyedListener>> mDestroyedListenerMap = new HashMap<>();

        private int mForegroundCount = 0;
        private int mConfigCount = 0;
        private boolean mIsBackground = false;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            setAnimatorsEnabled();
            setTopActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (!mIsBackground) {
                setTopActivity(activity);
            }
            if (mConfigCount < 0) {
                ++mConfigCount;
            } else {
                ++mForegroundCount;
            }
        }

        @Override
        public void onActivityResumed(final Activity activity) {
            setTopActivity(activity);
            if (mIsBackground) {
                mIsBackground = false;
                postStatus(true);
            }
            processHideSoftInputOnActivityDestroy(activity, false);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity.isChangingConfigurations()) {
                --mConfigCount;
            } else {
                --mForegroundCount;
                if (mForegroundCount <= 0) {
                    mIsBackground = true;
                    postStatus(false);
                }
            }
            processHideSoftInputOnActivityDestroy(activity, true);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {/**/}

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityList.remove(activity);
            consumeOnActivityDestroyedListener(activity);
            fixSoftInputLeaks(activity.getWindow());
        }

        Activity getTopActivity() {
            if (!mActivityList.isEmpty()) {
                for (int i = mActivityList.size() - 1; i >= 0; i--) {
                    Activity activity = mActivityList.get(i);
                    if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                        continue;
                    }
                    return activity;
                }
            }
            Activity topActivityByReflect = getTopActivityByReflect();
            if (topActivityByReflect != null) {
                setTopActivity(topActivityByReflect);
            }
            return topActivityByReflect;
        }

        void addOnAppStatusChangedListener(final Object object,
                                           final OnAppStatusChangedListener listener) {
            mStatusListenerMap.put(object, listener);
        }

        void removeOnAppStatusChangedListener(final Object object) {
            mStatusListenerMap.remove(object);
        }

        void removeOnActivityDestroyedListener(final Activity activity) {
            if (activity == null) return;
            mDestroyedListenerMap.remove(activity);
        }

        void addOnActivityDestroyedListener(final Activity activity,
                                            final OnActivityDestroyedListener listener) {
            if (activity == null || listener == null) return;
            Set<OnActivityDestroyedListener> listeners;
            if (!mDestroyedListenerMap.containsKey(activity)) {
                listeners = new HashSet<>();
                mDestroyedListenerMap.put(activity, listeners);
            } else {
                listeners = mDestroyedListenerMap.get(activity);
                assert listeners != null;
                if (listeners.contains(listener)) return;
            }
            listeners.add(listener);
        }

        /**
         * To solve close keyboard when activity onDestroy.
         * The preActivity set windowSoftInputMode will prevent
         * the keyboard from closing when curActivity onDestroy.
         */
        private void processHideSoftInputOnActivityDestroy(final Activity activity, boolean isSave) {
            if (isSave) {
                final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                final int softInputMode = attrs.softInputMode;
                activity.getWindow().getDecorView().setTag(-123, softInputMode);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            } else {
                final Object tag = activity.getWindow().getDecorView().getTag(-123);
                if (!(tag instanceof Integer)) return;
                Utils.runOnUiThreadDelayed(() -> activity.getWindow().setSoftInputMode(((Integer) tag)), 100);
            }
        }

        private void postStatus(final boolean isForeground) {
            if (mStatusListenerMap.isEmpty()) return;
            for (OnAppStatusChangedListener onAppStatusChangedListener : mStatusListenerMap.values()) {
                if (onAppStatusChangedListener == null) return;
                if (isForeground) {
                    onAppStatusChangedListener.onForeground();
                } else {
                    onAppStatusChangedListener.onBackground();
                }
            }
        }

        private void setTopActivity(final Activity activity) {
            if (PERMISSION_ACTIVITY_CLASS_NAME.equals(activity.getClass().getName())) return;
            if (mActivityList.contains(activity)) {
                if (!mActivityList.getLast().equals(activity)) {
                    mActivityList.remove(activity);
                    mActivityList.addLast(activity);
                }
            } else {
                mActivityList.addLast(activity);
            }
        }

        private void consumeOnActivityDestroyedListener(Activity activity) {
            Iterator<Map.Entry<Activity, Set<OnActivityDestroyedListener>>> iterator
                    = mDestroyedListenerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Activity, Set<OnActivityDestroyedListener>> entry = iterator.next();
                if (entry.getKey() == activity) {
                    Set<OnActivityDestroyedListener> value = entry.getValue();
                    for (OnActivityDestroyedListener listener : value) {
                        listener.onActivityDestroyed(activity);
                    }
                    iterator.remove();
                }
            }
        }

        private Activity getTopActivityByReflect() {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                Object currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread").invoke(null);
                Field mActivityListField = activityThreadClass.getDeclaredField("mActivityList");
                mActivityListField.setAccessible(true);
                Map activities = (Map) mActivityListField.get(currentActivityThreadMethod);
                if (activities == null) return null;
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        return (Activity) activityField.get(activityRecord);
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }


    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int getDimensPx(Context context, int dimen) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(dimen), context.getResources().getDisplayMetrics());
    }


    @SuppressLint("PrivateApi")
    public static int calcStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static boolean saveBitmap(Bitmap bitmap, String path, boolean recycle) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return false;
        }

        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = new FileOutputStream(path);
            bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            return true;

        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ignored) {
                }
            }
            if (recycle) {
                bitmap.recycle();
            }
        }
    }


    public static final class FileProvider4UtilCode extends FileProvider {

        @Override
        public boolean onCreate() {
            Utils.init(getContext());
            return true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    public abstract static class Task<Result> implements Runnable {

        private static final int NEW = 0;
        private static final int COMPLETING = 1;
        private static final int CANCELLED = 2;
        private static final int EXCEPTIONAL = 3;

        private volatile int state = NEW;

        abstract Result doInBackground();

        private final Callback<Result> mCallback;

        public Task(final Callback<Result> callback) {
            mCallback = callback;
        }

        @Override
        public void run() {
            try {
                final Result t = doInBackground();

                if (state != NEW) return;
                state = COMPLETING;
                UTIL_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onCall(t);
                    }
                });
            } catch (Throwable th) {
                if (state != NEW) return;
                state = EXCEPTIONAL;
            }
        }

        public void cancel() {
            state = CANCELLED;
        }

        public boolean isDone() {
            return state != NEW;
        }

        public boolean isCanceled() {
            return state == CANCELLED;
        }
    }


    public interface Callback<T> {
        void onCall(T data);
    }

    public interface OnAppStatusChangedListener {
        void onForeground();

        void onBackground();
    }

    public interface OnActivityDestroyedListener {
        void onActivityDestroyed(Activity activity);
    }

    public static String getText(EditText edt) {
        return edt == null ? "" : edt.getText().toString().trim();
    }

    public static String getText(TextView tv) {
        return tv == null ? "" : tv.getText().toString().trim();
    }

    public static int getColor(int resId) {
        return com.blankj.utilcode.util.Utils.getApp().getResources().getColor(resId);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getDrawable(int resId) {
        return com.blankj.utilcode.util.Utils.getApp().getResources().getDrawable(resId);
    }

    public static void startTrendVideoViewActivity(Activity activity, View view, VideoInfo videoInfo) {
        Intent intent = new Intent(activity, MyVideoViewActivity.class);
        intent.putExtra(Interfaces.EXTRA_VIDEO_INFO, videoInfo);
       /* if (view != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, view, getString(R.string.video_transitions));
            activity.startActivity(intent, optionsCompat.toBundle());
        } else {
        }*/
        activity.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    public static void setEditCardTextChangedListener(EditText editText, final TextView textView, final int maxLenght, boolean filterEnter) {
        if (textView != null) {
            textView.setText(StringUtils.judgeTextLength(getText(editText)) + "/" + maxLenght);
        }
        InputFilter[] filters = {new CustomFilter(maxLenght), (source, start, end, dest, dstart, dend) -> {
            if (filterEnter && source.toString().contains("\n")) {
                return source.toString().replace("\n", "");
            }
            return null;
        }};
        editText.setFilters(filters);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (textView != null) {
                    textView.setText(StringUtils.judgeTextLength(editable.toString()) + "/" + maxLenght);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public static void setEditCardTextChangedListener(EditText editText, final ImageView imageView, TextView errorHintView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                imageView.setVisibility(editable.toString().length() > 0 ? View.VISIBLE : View.GONE);
                if (editable.toString().length() > 0) {
                    errorHintView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 获取裁剪照片的Intent
     *
     * @param targetUri 要裁剪的照片
     * @param outPutUri 裁剪完成的照片
     * @param
     * @return
     */
    public static Intent getCropIntentWithOtherApp(Uri targetUri, Uri outPutUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(targetUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 800);
        intent.putExtra("aspectY", 800);
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        return intent;
    }

    public static void showDialog(final Activity activity, final String title, final String message,
                                  final String positiveText, final String negativeText,
                                  final DialogInterface.OnClickListener positiveListener,
                                  final DialogInterface.OnClickListener negativeListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(positiveText, positiveListener)
                        .setNegativeButton(negativeText, negativeListener)
                        .show();
            }
        });

    }
}
