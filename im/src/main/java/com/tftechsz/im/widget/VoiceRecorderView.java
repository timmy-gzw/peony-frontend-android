package com.tftechsz.im.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.utils.VoicePlayHelper;
import com.tftechsz.im.utils.VoiceRecorder;
import com.tftechsz.common.utils.log.KLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class VoiceRecorderView extends RelativeLayout {

    private static final String TAG = "VoiceRecorderView";

    private TextView tvHoldTalk;

    private LinearLayout llVoice;

    private VoiceRecorder mVoiceRecorder;
    private Handler mDialogHandler;
    private static final int CANCEL_RECORD = -300;
    private static final int MIN_TIME = 1000;
    private final static int MAX_TIME = 60000;
    private boolean isTooShort = true;


    private View ChatDialogView;
    private ImageView ivDialogShow;
    private TextView tvDialogHint;
    private Dialog recordDialog;
    private long mStartTime;

    private float y;
    /*允许手指上滑距离*/
    private final int FINGER_MOVEMENT = -80;

    private AnimationDrawable anim;

    private Context mContext;
    /* 完成录音回调*/
    private OnFinishedRecordListener finishedListener;
    /**
     * 语音播放工具
     */
    private VoicePlayHelper mVoicePlayerWrapper;
    private Disposable countDownTimer;

    private int bgColor = R.drawable.bg_gray;
    private int textColor = getResources().getColor(R.color.black);

    public VoiceRecorderView(Context context) {
        super(context);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_voice_recorder, this);
        llVoice = findViewById(R.id.ll_voice);
        tvHoldTalk = findViewById(R.id.tv_hold_talk);
       // llVoice.setBackgroundResource(bgColor);
        tvHoldTalk.setTextColor(textColor);
        llVoice.setOnTouchListener(new VoiceRecorderViewTouchListenner());/*设置按钮触摸事件*/


        initHandler();
        mVoiceRecorder = new VoiceRecorder(mDialogHandler);
    }

    public void setBackgroundResource(int res) {
        bgColor = res;
        llVoice.setBackgroundResource(res);
    }

    public void setTextColor(int color) {
        textColor = color;
        tvHoldTalk.setTextColor(color);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (countDownTimer != null && !countDownTimer.isDisposed()) {
            countDownTimer.dispose();
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mDialogHandler = new Handler() {
            private int lastWhat = -1;

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CANCEL_RECORD) {
                    onCancelRecord();
                    recordDialog.dismiss();
                } else if (msg.what != -1) {
                    if (!isTooShort && lastWhat != msg.what) {
                        lastWhat = msg.what;
                        Log.i(TAG, "AUDIO AMPLITUDE ==== " + lastWhat);
                        if (msg.what > 5) {
                            lastWhat = 5;
                        }
                    //    ivDialogShow.setBackgroundResource(VOICE_IMAGE_RES[lastWhat]);
                    }
                }
            }
        };
    }

    private void initDialogAndStartRecord() {
        mStartTime = System.currentTimeMillis();
        recordDialog = new Dialog(mContext, R.style.chat_recode_dialog_style);
        ChatDialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_chat_recode, null);
        ivDialogShow = ChatDialogView.findViewById(R.id.iv_chat_recode);
        tvDialogHint = ChatDialogView.findViewById(R.id.tv_chat_recode_hint);
        tvDialogHint.setText(mContext.getString(R.string.finger_slide_cancel_send));
        recordDialog.setContentView(ChatDialogView, new LinearLayout.LayoutParams(
                DensityUtils.dp2px(getContext(), 160),
                DensityUtils.dp2px(getContext(), 160)));
        WindowManager.LayoutParams lp = recordDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        recordDialog.show();
        isTooShort = false;
        boolean isFirst = true;
        onStartRecord();
    }

    private void onStopRecord() {
        VoiceRecorder.VoiceRecorderBean voiceRecorderBean = null;
        try {
            voiceRecorderBean = mVoiceRecorder.stopRecoding();
        } catch (Exception e) {
            KLog.e("=========" + e.getMessage());
            e.printStackTrace();
        }
        if (null != finishedListener && null != voiceRecorderBean) {
            if (voiceRecorderBean.getRecord_state() == VoiceRecorder.RECORDER_SUCCESS) {
//                LogUtil.e(TAG,"RECORD STATE = " + voiceRecorderBean.getRecord_state() +"VOICE PATH = " + voiceRecorderBean.getRecord_path() + "VOICE TIME LENGTH = " + voiceRecorderBean.getRecord_time());
                finishedListener.onFinishedRecord(voiceRecorderBean.getRecord_path(), voiceRecorderBean.getRecord_time());
            }
        }
    }


    private void onStartRecord() {
        startCountdown();
        mVoiceRecorder.startRecording(mContext);
    }

    private void startCountdown() {
        countDownTimer = Observable.timer(60 * 1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        VoiceRecorderView.this.onFinishRecord(60 * 1000);
                        tvHoldTalk.setText(mContext.getString(R.string.hold_talk));
                        llVoice.setBackgroundResource(bgColor);
                    }
                });
    }

    private void stopCountdown() {
        if (countDownTimer != null && !countDownTimer.isDisposed()) {
            countDownTimer.dispose();
            countDownTimer = null;
        }
    }

    public void setVoicePlayerWrapper(VoicePlayHelper voicePlayerWrapper) {
        mVoicePlayerWrapper = voicePlayerWrapper;
    }

    /**
     * 音频录制视图触摸事件监听
     */
    private class VoiceRecorderViewTouchListenner implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            llVoice.requestDisallowInterceptTouchEvent(true);/*设置父View不在拦截当前View事件*/

            int action = event.getAction();
            y = event.getY();

            if (!isTooShort) {
                if (ivDialogShow != null && y < FINGER_MOVEMENT) {
        //            ChatDialogView.setBackgroundResource(R.color.red_ff9999);
                    ivDialogShow.setBackgroundResource(R.mipmap.icon_cancel_recode1);
                    tvDialogHint.setText(mContext.getString(R.string.finger_loosening_cancel_send));
//                    anim.stop();
                } else if (ivDialogShow != null) {
                    ChatDialogView.setBackgroundResource(R.color.transparent);
                    tvDialogHint.setText(mContext.getString(R.string.finger_slide_cancel_send));
//                    anim = (AnimationDrawable) ivDialogShow.getBackground();
//                    if (isFirst) {
//                        anim.start();
//                        isFirst = false;
//                    }
//                    anim.start();
                }
            }

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mVoicePlayerWrapper != null) {//存在正在播放的语音则停止播放
                        mVoicePlayerWrapper.stopVoice();
                    }
                    tvHoldTalk.setText(mContext.getString(R.string.release_end));
                    llVoice.setBackgroundResource(bgColor);
                    initDialogAndStartRecord();
//                    anim = (AnimationDrawable) ivDialogShow.getBackground();
//                    anim.start();
                    break;
                case MotionEvent.ACTION_UP:
                    tvHoldTalk.setText(mContext.getString(R.string.hold_talk));
                    llVoice.setBackgroundResource(bgColor);
                    int diffTime = (int) (System.currentTimeMillis() - mStartTime);
                    if (y >= FINGER_MOVEMENT && (diffTime <= MAX_TIME)) {
                        onFinishRecord(diffTime);
                    } else if (y < FINGER_MOVEMENT) {  //当手指向上滑，会cancel
                        onCancelRecord();
                        recordDialog.dismiss();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL: // 取消
                    tvHoldTalk.setText(mContext.getString(R.string.hold_talk));
                    llVoice.setBackgroundResource(bgColor);
                    onCancelRecord();
                    recordDialog.dismiss();
                    break;
            }
            return true;
        }
    }

    private void onCancelRecord() {
        stopCountdown();
        mVoiceRecorder.discardRecording();
    }

    /**
     * 完成录音
     */
    @SuppressLint("CheckResult")
    private void onFinishRecord(int diffTime) {
        if (diffTime < MIN_TIME) {
            mDialogHandler.sendEmptyMessageDelayed(CANCEL_RECORD, 1000);
            ivDialogShow.setBackgroundResource(R.mipmap.icon_error_recode);
            tvDialogHint.setText(mContext.getString(R.string.Recording_time_is_too_short));
            tvDialogHint.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
//            anim.stop();
            isTooShort = true;
            Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            recordDialog.dismiss();
                        }
                    });
            return;
        } else {
            onStopRecord();
            recordDialog.dismiss();
        }
        stopCountdown();
    }

    public interface OnFinishedRecordListener {
        void onFinishedRecord(String audioPath, int time);
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }
}
