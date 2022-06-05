package com.tftechsz.party.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.UnReadMessageEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.party.R;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.dto.PartyDto;
import com.tftechsz.party.mvp.ui.activity.PartyRoomActivity;

import io.reactivex.disposables.CompositeDisposable;

public class FloatPartyWindowService extends Service {
    protected String mRoomId = "";
    private String mRoomThumb;
    private String mRoomBg;
    private int mFightPattern;
    private int mIndex;
    protected int mId;
    protected int mMessageNum;  // 消息数量
    private boolean mIsOnSeat;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private ImageView mIvThumb;
    //view
    private View mFloatingLayout;    //浮动布局
    protected CompositeDisposable mCompositeDisposable;
    private CustomPopWindow customPopWindow;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public class MyBinder extends Binder {
        public FloatPartyWindowService getService() {
            return FloatPartyWindowService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCompositeDisposable = new CompositeDisposable();
        initWindow();
        initFloating();
        //判断是否有消息
        mCompositeDisposable.add(RxBus.getDefault().toObservable(UnReadMessageEvent.class)
                .subscribe(
                        event -> {
                            mMessageNum = event.p2pNumber;
                        }
                ));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mRoomId = intent.getStringExtra("roomId");
            mId = intent.getIntExtra("id", 0);
            mRoomBg = intent.getStringExtra("roomBg");
            mRoomThumb = intent.getStringExtra("roomThumb");
            mMessageNum = intent.getIntExtra("messageNum", 0);
            mIndex = intent.getIntExtra("index", 0);
            mFightPattern = intent.getIntExtra("fightPattern", 0);
            mIsOnSeat = intent.getBooleanExtra("isOnSeat", false);
            MMKVUtils.getInstance().encode("index",mIndex);
            GlideUtils.loadCircleImage(this, mIvThumb, mRoomThumb, R.drawable.party_bg_default);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public boolean stopService(Intent name) {
        Utils.runOnUiThread(() -> {
            if (mFloatingLayout != null && mWindowManager != null) {
                // 移除悬浮窗口
                mWindowManager.removeView(mFloatingLayout);
                mFloatingLayout = null;
            }
        });
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.runOnUiThread(() -> {
            if (mFloatingLayout != null && mWindowManager != null) {
                // 移除悬浮窗口
                mWindowManager.removeView(mFloatingLayout);
                mFloatingLayout = null;
            }
        });
    }


    public void stop(){
        boolean isOnSeat = MMKVUtils.getInstance().decodeBoolean(Constants.PARTY_IS_ON_SEAT);
        int id =  MMKVUtils.getInstance().decodeInt(Constants.PARTY_ID);
        if(isOnSeat){
            int index = MMKVUtils.getInstance().decodeInt("index");
            leaveSeat(id, index, 1);
        }
        joinLeaveParty("close", id);
    }

    /**
     * 设置悬浮框基本参数（位置、宽高等）
     */
    private void initWindow() {
        mWindowManager = (WindowManager) BaseApplication.getInstance().getSystemService(WINDOW_SERVICE);
        wmParams = getParams();//设置好悬浮窗的参数
        // 悬浮窗默认显示以左上角为起始坐标
        wmParams.gravity = Gravity.END | Gravity.TOP;
        //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
        wmParams.x = 0;
        wmParams.y = ScreenUtil.getDisplayHeight() - DensityUtils.dp2px(BaseApplication.getInstance(), 160);
        //得到容器，通过这个inflater来获得悬浮窗控件
        LayoutInflater inflater = LayoutInflater.from(BaseApplication.getInstance());
        // 获取浮动窗口视图所在布局
        mFloatingLayout = inflater.inflate(R.layout.layout_float_party, null);
        mFloatingLayout.findViewById(R.id.iv_close_float).setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if ((now - currentTime) > 1500) {
                if (mFightPattern == 2 && mIsOnSeat) {
                    showEndPkPop(this);
                    return;
                }
                stopAudioService();
            }
        });
        mIvThumb = mFloatingLayout.findViewById(R.id.iv_thumb);
        // 添加悬浮窗的视图
        mWindowManager.addView(mFloatingLayout, wmParams);
    }


    public void stopAudioService() {
        if (mIsOnSeat) {
            leaveSeat(mId, mIndex, 1);
        }
        if (BaseMusicHelper.get().getPlayService() != null)
            BaseMusicHelper.get().getPlayService().stop();
        joinLeaveParty("close", mId);
        NIMClient.getService(ChatRoomService.class).exitChatRoom(mRoomId);
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().releaseAudience();
        }
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().onDestroy();
        }
        stopSelf();
    }

    public boolean isOnSeat() {
        return mIsOnSeat;
    }


    /**
     * 显示结束PK
     *
     * @param activity
     */
    public void showEndPkPop(FloatPartyWindowService activity) {
        if (customPopWindow == null)
            customPopWindow = new CustomPopWindow(activity);
        customPopWindow.setContent("当前正在PK中，无法关闭派对");
        customPopWindow.setSingleButtong("我知道了", Utils.getColor(R.color.color_normal));
        customPopWindow.showPopupWindow();
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
        wmParams.width = DensityUtils.dp2px(BaseApplication.getInstance(), 100);        //窗口的宽
        wmParams.height = DensityUtils.dp2px(BaseApplication.getInstance(), 100);
        return wmParams;
    }

    private long currentTime;

    private void initFloating() {
//        //容器父布局
        FrameLayout smallSizePreviewLayout = mFloatingLayout.findViewById(R.id.fl_content);
        //悬浮框点击事件
        smallSizePreviewLayout.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if ((now - currentTime) > 1500) {
                currentTime = System.currentTimeMillis();
                Intent intent = new Intent(FloatPartyWindowService.this, PartyRoomActivity.class);
                intent.putExtra("roomId", mRoomId);
                intent.putExtra("id", mId);
                intent.putExtra("roomBg", mRoomBg);
                intent.putExtra("roomThumb", mRoomThumb);
                intent.putExtra("messageNum", mMessageNum);
                intent.putExtra("lastPartyId", mId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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


    /**
     * 下麦
     */
    public void leaveSeat(int id, int index, int status) {
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(RetrofitManager.getInstance().createPartyApi(PartyApiService.class).leaveSeat(id, index, status).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 加入离开party
     */
    public void joinLeaveParty(String from_type, int id) {
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(RetrofitManager.getInstance().createIMApi(PartyApiService.class).joinLeaveParty(from_type, id,1).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyDto> response) {

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

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
                    if (mFloatingLayout != null)
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

