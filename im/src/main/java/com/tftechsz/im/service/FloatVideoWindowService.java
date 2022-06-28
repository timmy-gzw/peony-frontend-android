package com.tftechsz.im.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.tftechsz.im.R;
import com.tftechsz.im.mvp.ui.activity.VideoCallActivity;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.utils.Utils;

public class FloatVideoWindowService extends Service {

    public static final String INVENT_EVENT = "call_in_event";
    public static final String CALL_OUT_USER = "call_out_user";
    public static final String CALL_DIR = "call_dir";
    public static final String CALL_TYPE = "call_type";
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    public static boolean isSmall = false;
    //view
    private View mFloatingLayout;    //浮动布局
    private Chronometer chronometer;

    private UserInfo callOutUser;//呼出用户
    private InvitedEvent invitedEvent;//来电事件
    private int callDir;//0 call out 1 call in
    private int mChannelType;  // 1 语音， 2 视频

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        invitedEvent = (InvitedEvent) intent.getSerializableExtra(INVENT_EVENT);
        callOutUser = (UserInfo) intent.getSerializableExtra(CALL_OUT_USER);
        callDir = intent.getIntExtra(CALL_DIR, 0);
        mChannelType = intent.getIntExtra(CALL_TYPE, 1);
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public FloatVideoWindowService getService() {
            return FloatVideoWindowService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();//设置悬浮窗基本参数（位置、宽高等）
        initFloating();//悬浮框点击事件的处理
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.runOnUiThread(() -> {
            if (mFloatingLayout != null) {
                // 移除悬浮窗口
                mWindowManager.removeView(mFloatingLayout);
                isSmall = false;
            }
            if (chronometer != null) {
                chronometer.stop();
                chronometer = null;
            }
        });

    }

    /**
     * 设置悬浮框基本参数（位置、宽高等）
     */
    private void initWindow() {

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        wmParams = getParams();//设置好悬浮窗的参数
        // 悬浮窗默认显示以左上角为起始坐标
        wmParams.gravity = Gravity.END | Gravity.TOP;
        //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
        wmParams.x = 0;
        wmParams.y = 210;
        //得到容器，通过这个inflater来获得悬浮窗控件
        LayoutInflater inflater = LayoutInflater.from(this);
        // 获取浮动窗口视图所在布局
        mFloatingLayout = inflater.inflate(R.layout.layout_float_voice_layout, null);
        // 添加悬浮窗的视图
        mWindowManager.addView(mFloatingLayout, wmParams);
        isSmall = true;
    }

    private WindowManager.LayoutParams getParams() {
        wmParams = new WindowManager.LayoutParams();
        //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
        //悬浮窗的显示问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.format = PixelFormat.RGBA_8888;
        //设置可以显示在状态栏上
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        //设置悬浮窗口长宽数据
        wmParams.width = DensityUtils.dp2px(this, 100);        //窗口的宽
        wmParams.height = DensityUtils.dp2px(this, 100);
        return wmParams;
    }

    private void initFloating() {
        VideoCallActivity.endTime = System.currentTimeMillis();
        long timeDiff = VideoCallActivity.endTime - VideoCallActivity.startTime;
//        //容器父布局
        LinearLayout smallSizePreviewLayout = mFloatingLayout.findViewById(R.id.ll_content);
        chronometer = mFloatingLayout.findViewById(R.id.chronometer2);
        chronometer.setBase(SystemClock.elapsedRealtime() - VideoCallActivity.seconds * 1000 - timeDiff);
        chronometer.start();
        //悬浮框点击事件
        smallSizePreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloatVideoWindowService.this, VideoCallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(CALL_DIR, callDir);
                intent.putExtra(CALL_TYPE, mChannelType);
                intent.putExtra(INVENT_EVENT, invitedEvent);
                intent.putExtra(CALL_OUT_USER, callOutUser);
                startActivity(intent);
//                VideoCallActivity.ISBOUND = true;
            }
        });
//        //悬浮框触摸事件，设置悬浮框可拖动
        smallSizePreviewLayout.setOnTouchListener(new FloatingListener());
    }

    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY;
    //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
    private boolean isMove;

    private class FloatingListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
//                    wmParams.x += mTouchCurrentX - mTouchStartX;
                    wmParams.x += mTouchStartX - mTouchCurrentX;
                    wmParams.y += mTouchCurrentY - mTouchStartY;
                    mWindowManager.updateViewLayout(mFloatingLayout, wmParams);

                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
            }
            //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
            return isMove;
        }
    }
}

