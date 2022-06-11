package com.tftechsz.mine.mvp.ui.activity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nis.alivedetected.ActionType;
import com.netease.nis.alivedetected.AliveDetector;
import com.netease.nis.alivedetected.DetectedListener;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActFaceCheckBinding;
import com.tftechsz.mine.databinding.LayoutTvStepBinding;
import com.tftechsz.mine.entity.FaceCheckErrorBean;
import com.tftechsz.mine.mvp.IView.IRealAuthView;
import com.tftechsz.mine.mvp.presenter.RealAuthPresenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import static com.netease.nis.alivedetected.ActionType.ACTION_ERROR;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : TODO
 */
@Route(path = ARouterApi.ACTIVITY_FACE_CHECK)
public class FaceCheckActivity extends BaseMvpActivity<IRealAuthView, RealAuthPresenter> implements IRealAuthView, CustomPopWindow.OnSelectListener {
    private AliveDetector mAliveDetector;
    private final boolean isUsedCustomStateTip = true; // 是否使用自定义活体状态文案
    private static final String BUSINESS_ID = "e1fe05ed42df45cdb52d8ea152504b34";
    private final Map<String, String> stateTipMap = new HashMap();
    private static final String KEY_STRAIGHT_AHEAD = "straight_ahead";
    private static final String KEY_OPEN_MOUTH = "open_mouth";
    private static final String KEY_TURN_HEAD_TO_LEFT = "turn_head_to_left";
    private static final String KEY_TURN_HEAD_TO_RIGHT = "turn_head_to_right";
    private static final String KEY_BLINK_EYES = "blink_eyes";
    private static final String FRONT_ERROR_TIP = "请移动人脸到摄像头视野中间";
    private static final String TIP_STRAIGHT_AHEAD = "请正对手机屏幕\n" +
            "将面部移入框内";//"请将面部移入框内并保持不动";
    private static final String TIP_OPEN_MOUTH = "张张嘴";
    private static final String TIP_TURN_HEAD_TO_LEFT = "慢慢左转头";
    private static final String TIP_TURN_HEAD_TO_RIGHT = "慢慢右转头";
    private static final String TIP_BLINK_EYES = "眨眨眼";
    private int mCurrentCheckStepIndex = 0;
    private ActionType mCurrentActionType = ActionType.ACTION_STRAIGHT_AHEAD;
    private ActionType[] mActions;
    private boolean isOpenVoice = true;
    private final MediaPlayer mPlayer = new MediaPlayer();
    private CustomPopWindow mPopWindow;
    private ActFaceCheckBinding mBind;
    private LayoutTvStepBinding mStepBinding2, mStepBinding3, mStepBinding4;
    private boolean mIsPartyMode;

    @Override
    public RealAuthPresenter initPresenter() {
        return new RealAuthPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_face_check);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).barColor(R.color.white).fitsSystemWindows(true).init();
        mBind.surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mBind.imgBtnBack.setOnClickListener(view -> {
            if (mAliveDetector != null) {
                mAliveDetector.stopDetect();
            }
            finish();
        });
        mBind.ivVoice.setOnClickListener(v -> {
            isOpenVoice = !isOpenVoice;
            if (isOpenVoice) {
                mBind.ivVoice.setImageResource(R.mipmap.ico_voice_open_2x);
            } else {
                mBind.ivVoice.setImageResource(R.mipmap.ico_voice_close_2x);
            }
        });
        Utils.setWindowBrightness(this, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        initRxBus();
        mIsPartyMode = getIntent().getBooleanExtra(Interfaces.EXTRA_DATA, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_FINISH_REAL) {
                                finish();
                            }
                        }
                ));
    }

    @Override
    protected void initData() {
        stateTipMap.put(KEY_STRAIGHT_AHEAD, TIP_STRAIGHT_AHEAD);
        stateTipMap.put(KEY_TURN_HEAD_TO_LEFT, TIP_TURN_HEAD_TO_LEFT);
        stateTipMap.put(KEY_TURN_HEAD_TO_RIGHT, TIP_TURN_HEAD_TO_RIGHT);
        stateTipMap.put(KEY_OPEN_MOUTH, TIP_OPEN_MOUTH);
        stateTipMap.put(KEY_BLINK_EYES, TIP_BLINK_EYES);
        mAliveDetector = AliveDetector.getInstance();
        mAliveDetector.setDebugMode(AppUtils.isAppDebug());
        mAliveDetector.init(this, mBind.surfaceView, BUSINESS_ID);
        mAliveDetector.setDetectedListener(new DetectedListener() {
            @Override
            public void onReady(boolean isInitSuccess) {
                if (isInitSuccess) {
                    Log.d(TAG, "活体检测引擎初始化完成");
                } else {
                    //  mAliveDetector.startDetect();
                    Log.e(TAG, "活体检测引擎初始化失败");
                }
            }

            @Override
            public void onCheck() {
                Utils.logE("onCheck");
            }

            /**
             * 此次活体检测下发的待检测动作指令序列
             *
             * @param actionTypes 1
             */
            @Override
            public void onActionCommands(ActionType[] actionTypes) {
                mActions = actionTypes;
                String commands = buildActionCommand(actionTypes);
                showIndicatorOnUiThread(commands.length() - 1);
//                showToast("活体检测动作序列为:" + commands);
                Log.e(TAG, "活体检测动作序列为:" + commands);
            }

            @Override
            public void onStateTipChanged(ActionType actionType, String stateTip) {
                Log.d(TAG, "actionType:" + actionType.getActionTip() + " stateTip:" + actionType + " CurrentCheckStepIndex:" + mCurrentCheckStepIndex);
                if (actionType == ActionType.ACTION_PASSED && !actionType.getActionID().equals(mCurrentActionType.getActionID())) {
                    mCurrentCheckStepIndex++;
                    if (mCurrentCheckStepIndex < mActions.length) {
                        updateIndicatorOnUiThread(mCurrentCheckStepIndex);
                        if (isOpenVoice) {
                            playSounds(mCurrentCheckStepIndex);
                        }
                        mCurrentActionType = mActions[mCurrentCheckStepIndex];
                    }
                }
                if (isUsedCustomStateTip) {
                    switch (actionType) {
                        case ACTION_STRAIGHT_AHEAD:
                            setTipText("", false);
                            break;
                        case ACTION_OPEN_MOUTH:
                            setTipText(stateTipMap.get(KEY_OPEN_MOUTH), false);
                            break;
                        case ACTION_TURN_HEAD_TO_LEFT:
                            setTipText(stateTipMap.get(KEY_TURN_HEAD_TO_LEFT), false);
                            break;
                        case ACTION_TURN_HEAD_TO_RIGHT:
                            setTipText(stateTipMap.get(KEY_TURN_HEAD_TO_RIGHT), false);
                            break;
                        case ACTION_BLINK_EYES:
                            setTipText(stateTipMap.get(KEY_BLINK_EYES), false);
                            break;
                        case ACTION_ERROR:
                            setTipText(stateTip, true);
                            break;
//                        case ACTION_PASSED:
//                            setTipText(stateTip);
//                            break;
                    }
                } else {
                    setTipText(stateTip, actionType == ACTION_ERROR);
                }
            }

            @Override
            public void onPassed(boolean isPassed, String token) {
                runOnUiThread(() -> {
                    GlobalDialogManager.getInstance().show(getFragmentManager(), "认证中，请稍等...");
                    mBind.pvCountTime.cancelCountTimeAnimation();
                    if (isPassed) {
                        Log.d(TAG, "活体检测通过,token is:" + token);
                        if (mIsPartyMode) { //如果是派对模式
                            p.partyRecheck(token);
                        } else {
                            p.recheck(token);
                        }

                    } else {
                        Log.e(TAG, "活体检测不通过,token is:" + token);
                        jump2FailureActivity(token, -999, null);
                    }
                });
            }


            @Override
            public void onError(int code, String msg, String token) {
                Log.e(TAG, "listener [onError] 活体检测出错,原因:" + msg + " token:" + token);
                jump2FailureActivity(token, code, msg);
            }

            @Override
            public void onOverTime() {
                runOnUiThread(() -> {
                    mBind.pvCountTime.cancelCountTimeAnimation();
                    new CustomPopWindow(mContext, 1).setLeftButton("退出")
                            .setRightButton("重试")
                            .setTitle("检测超时")
                            .setContent("请在规定时间内完成动作")
                            .addOnClickListener(FaceCheckActivity.this)
                            .setOutSideDismiss(false)
                            .setBackPressEnable(false)
                            .showPopupWindow();
                });
            }
        });
        mAliveDetector.setSensitivity(AliveDetector.SENSITIVITY_NORMAL);
        mAliveDetector.setTimeOut(1000 * 30);
        mAliveDetector.startDetect();
    }

    private void initCountTimeView() {
        mBind.pvCountTime.setStartAngle(0);
        mBind.pvCountTime.startCountTimeAnimation();
    }

    public static String buildActionCommand(ActionType[] actionCommands) {
        StringBuilder commands = new StringBuilder();
        for (ActionType actionType : actionCommands) {
            commands.append(actionType.getActionID());
        }
        return TextUtils.isEmpty(commands.toString()) ? "" : commands.toString();
    }

    private void setTipText(final String tip, final boolean isErrorType) {
        runOnUiThread(() -> {
            if (isErrorType) {
                if (FRONT_ERROR_TIP.equals(tip)) {
                    mBind.tvErrorTip.setText(TIP_STRAIGHT_AHEAD);
                } else {
                    mBind.tvErrorTip.setText(tip);
                }
                mBind.viewTipBackground.setVisibility(View.VISIBLE);
                mBind.blurView.setVisibility(View.VISIBLE);

            } else {
                mBind.viewTipBackground.setVisibility(View.INVISIBLE);
                mBind.blurView.setVisibility(View.INVISIBLE);
                mBind.tvTip.setText(tip);
                mBind.tvErrorTip.setText("");
            }
        });
    }


    private void showIndicatorOnUiThread(final int commandLength) {
        runOnUiThread(() -> {
            initCountTimeView();
            showViewStub(commandLength);
            showIndicator(commandLength);
        });
    }

    private void showIndicator(int commandLength) {
        switch (commandLength) {
            case 2:
                mStepBinding2.stepTv.setVisibility(View.VISIBLE);
                break;

            case 3:
                mStepBinding2.stepTv.setVisibility(View.VISIBLE);
                mStepBinding3.stepTv.setVisibility(View.VISIBLE);
                break;

            case 4:
                mStepBinding2.stepTv.setVisibility(View.VISIBLE);
                mStepBinding3.stepTv.setVisibility(View.VISIBLE);
                mStepBinding4.stepTv.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showViewStub(int commandLength) {
        ViewStub viewStub2 = mBind.vsStep2.getViewStub();
        ViewStub viewStub3 = mBind.vsStep3.getViewStub();
        ViewStub viewStub4 = mBind.vsStep4.getViewStub();
        if (commandLength == 2) {
            if (viewStub2 != null) {
                View inflate2 = viewStub2.inflate();
                mStepBinding2 = DataBindingUtil.getBinding(inflate2);
            }
        }

        if (commandLength == 3) {
            if (viewStub2 != null) {
                View inflate2 = viewStub2.inflate();
                mStepBinding2 = DataBindingUtil.getBinding(inflate2);
            }
            if (viewStub3 != null) {
                View inflate3 = viewStub3.inflate();
                mStepBinding3 = DataBindingUtil.getBinding(inflate3);
            }
        }

        if (commandLength == 4) {
            if (viewStub2 != null) {
                View inflate2 = viewStub2.inflate();
                mStepBinding2 = DataBindingUtil.getBinding(inflate2);
            }
            if (viewStub3 != null) {
                View inflate3 = viewStub3.inflate();
                mStepBinding3 = DataBindingUtil.getBinding(inflate3);
            }
            if (viewStub4 != null) {
                View inflate4 = viewStub4.inflate();
                mStepBinding4 = DataBindingUtil.getBinding(inflate4);
            }
        }
    }


    private void updateIndicatorOnUiThread(final int currentActionIndex) {
        runOnUiThread(() -> {
            updateIndicator(currentActionIndex);
            if (!isDestroy(this)) {
                updateGif(currentActionIndex);
            }
        });
    }

    private void resetIndicator() {
        mCurrentCheckStepIndex = 0;
        mCurrentActionType = ActionType.ACTION_STRAIGHT_AHEAD;
        mBind.tvStep1.setText("1");
        if (mStepBinding2 != null && mStepBinding2.stepTv.getVisibility() == View.VISIBLE) {
            mStepBinding2.setText("");
            setTextViewUnFocus(mStepBinding2.stepTv);
        }

        if (mStepBinding3 != null && mStepBinding3.stepTv.getVisibility() == View.VISIBLE) {
            mStepBinding3.setText("");
            setTextViewUnFocus(mStepBinding3.stepTv);
        }
        if (mStepBinding4 != null && mStepBinding4.stepTv.getVisibility() == View.VISIBLE) {
            mStepBinding4.setText("");
            setTextViewUnFocus(mStepBinding4.stepTv);
        }
    }

    private void resetGif() {
        runOnUiThread(() -> mBind.gifAction.setImageResource(R.mipmap.pic_front_2x));
    }

    private void updateIndicator(int currentActionPassedCount) {
        switch (currentActionPassedCount) {
            case 2:
                mBind.tvStep1.setText("");
                mStepBinding2.setText("2");
                setTextViewFocus(mStepBinding2.stepTv);
                break;

            case 3:
                mBind.tvStep1.setText("");
                mStepBinding2.setText("2");
                setTextViewFocus(mStepBinding2.stepTv);
                mStepBinding3.setText("3");
                setTextViewFocus(mStepBinding3.stepTv);
                break;

            case 4:
                mBind.tvStep1.setText("");
                mStepBinding2.setText("2");
                setTextViewFocus(mStepBinding2.stepTv);
                mStepBinding3.setText("3");
                setTextViewFocus(mStepBinding3.stepTv);
                mStepBinding4.setText("4");
                setTextViewFocus(mStepBinding4.stepTv);
                break;
        }
    }

    public static boolean isDestroy(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    private void updateGif(int currentActionIndex) {
        switch (mActions[currentActionIndex]) {
            case ACTION_TURN_HEAD_TO_LEFT:
                Glide.with(this).asGif().load(R.drawable.turn_left).diskCacheStrategy(DiskCacheStrategy.NONE).into(mBind.gifAction);
                //givAction.setImageResource(R.drawable.turn_left);
                break;

            case ACTION_TURN_HEAD_TO_RIGHT:
                Glide.with(this).asGif().load(R.drawable.turn_right).diskCacheStrategy(DiskCacheStrategy.NONE).into(mBind.gifAction);
                //givAction.setImageResource(R.drawable.turn_right);
                break;

            case ACTION_OPEN_MOUTH:
                Glide.with(this).asGif().load(R.drawable.open_mouth).diskCacheStrategy(DiskCacheStrategy.NONE).into(mBind.gifAction);
                //givAction.setImageResource(R.drawable.open_mouth);
                break;

            case ACTION_BLINK_EYES:
                Glide.with(this).asGif().load(R.drawable.open_eyes).diskCacheStrategy(DiskCacheStrategy.NONE).into(mBind.gifAction);
                //givAction.setImageResource(R.drawable.open_eyes);
                break;
        }
    }

    private void playSounds(int currentActionIndex) {
        switch (mActions[currentActionIndex]) {
            case ACTION_TURN_HEAD_TO_LEFT:
                playSound(getAssetFileDescriptor("turn_head_to_left.wav"));
                break;
            case ACTION_TURN_HEAD_TO_RIGHT:
                playSound(getAssetFileDescriptor("turn_head_to_right.wav"));
                break;
            case ACTION_OPEN_MOUTH:
                playSound(getAssetFileDescriptor("open_mouth.wav"));
                break;
            case ACTION_BLINK_EYES:
                playSound(getAssetFileDescriptor("blink_eyes.wav"));
                break;
        }
    }


    private void setTextViewFocus(TextView tv) {
        tv.setBackgroundDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.circle_tv_focus));
    }

    private void setTextViewUnFocus(TextView tv) {
        tv.setBackgroundDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.circle_tv_un_focus));
    }

    private AssetFileDescriptor getAssetFileDescriptor(String assetName) {
        try {
            AssetFileDescriptor fileDescriptor = getApplication().getAssets().openFd(assetName);
            return fileDescriptor;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getAssetFileDescriptor error" + e.toString());
        }
        return null;
    }

    private void playSound(AssetFileDescriptor fileDescriptor) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "playSound error" + e.toString());
        }
    }

    private void jump2FailureActivity(String token, int code, String msg) {
        ARouter.getInstance().navigation(MineService.class)
                .trackEvent("活体检测出错", Interfaces.POINT_EVENT_CALLBACK, "face_check_error",
                        JSON.toJSONString(new FaceCheckErrorBean(token, code, msg)), null);

        hideLoadingDialog();
        runOnUiThread(() -> {
            mBind.gifAction.setImageResource(R.mipmap.pic_front_2x);
            new CustomPopWindow(mContext, 1).setLeftButton("退出")
                    .setRightButton("重试")
                    .setTitle("检测错误")
                    .setContent("活体检测不通过, 请重试!")
                    .addOnClickListener(FaceCheckActivity.this)
                    .setOutSideDismiss(false)
                    .setBackPressEnable(false)
                    .showPopupWindow();
        });
    }

    @Override
    public void uploadRealAvatarSuccess(Boolean data) {

    }

    @Override
    public void uploadAvatarSuccess(String data) {

    }

    @Override
    public void facedetectCheckSuccess(RealCheckDto data) {

    }

    @Override
    public void recheckSuccess(RealCheckDto data) {
        hideLoadingDialog();
        if (data.pass) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FINISH_REAL));
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
            toastTip(data.msg);
        } else {
            if (mPopWindow == null) {
                mPopWindow = new CustomPopWindow(mContext, 1).setLeftButton("退出").setRightButton("重试").addOnClickListener(this);
            }
            mPopWindow.setContent(data.msg).setOutSideDismiss(false).setBackPressEnable(false).showPopupWindow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setWindowBrightness(this, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
        if (mAliveDetector != null) {
            mAliveDetector.stopDetect();
        }
        mBind.pvCountTime.cancelCountTimeAnimation();
    }

    @Override
    public void onCancel() {
        finish();
    }

    @Override
    public void onSure() {
        resetIndicator();
        resetGif();
        mBind.pvCountTime.startCountTimeAnimation();
        mAliveDetector.startDetect();
    }
}
