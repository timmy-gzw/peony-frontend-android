package com.tftechsz.mine.mvp.ui.activity;

import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.PartyService;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IVoiceSignView;
import com.tftechsz.mine.mvp.presenter.VoiceSignPresenter;
import com.tftechsz.mine.widget.CircleProgressBar;
import com.tftechsz.mine.widget.VoiceImageView;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import jaygoo.widget.wlv.WaveLineView;

public abstract class BaseVoiceRecordActivity extends BaseMvpActivity<IVoiceSignView, VoiceSignPresenter> implements View.OnClickListener, IVoiceSignView {
    public ImageView recordIcon;
    public ImageView recording;
    public ImageView recordPaly;
    public ImageView recordPalyPause;
    public WaveLineView waveLineView;
    public File mAudioDir;
    public static final String AUDIO_DIR_NAME = "PeonyRecord";
    public int MAX_VOICE_TIME = 20 * 1000;//录音最长时间
    public MediaRecorder mMediaRecorder;
    public AudioPlayer mediaPlayer;
    public String fileName;
    public String filePath;
    public CircleProgressBar mCircleProgressBar;
    public float progress;
    public TextView recordTip;
    public LinearLayout recordAgain;
    public LinearLayout recordUpload, llRecordView;
    public Chronometer recordTime;

    public TextView tvRecordMsg, tvDuration,toolbarTitle;
    public VoiceImageView voiceImageView;   //录音动画
    public int mRecordTime;
    public boolean touched = false; // 是否按着
    public CountDownTimer mCountDownTimer;
    private CustomPopWindow popWindow;
    public PartyService partyService;


    @Override
    public VoiceSignPresenter initPresenter() {
        return new VoiceSignPresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 修改成每次进入唯一，后面覆盖上一次
         */
        fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".aac";
        partyService = ARouter.getInstance().navigation(PartyService.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlay();
    }

    public void setMaxTime(long time, int endTime, float pro) {
        mCountDownTimer = new CountDownTimer(time, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                //20000/100=200次
                progress += pro;
                mCircleProgressBar.update(progress);
            }

            @Override
            public void onFinish() {
                touched = false;
                try {
                    endRecord(endTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_record_icon) {

        } else if (view.getId() == R.id.iv_record_ing) {
            recordTip.setText("点击播放");
        } else if (view.getId() == R.id.iv_record_play) {
            if (filePath == null) {
                filePath = mAudioDir + File.separator + fileName;
            }
            File file = new File(filePath);
            if (file.exists()) {
                startPlay(file);
            } else {
                recordIcon.setVisibility(View.VISIBLE);//录制图标可见
                recordPalyPause.setVisibility(View.GONE);//录制播放暂停图标不可见
                recordPaly.setVisibility(View.GONE);//录制播放图标不可见
                recording.setVisibility(View.GONE);//录制中可见
                recordAgain.setVisibility(View.GONE);//重新录制图标不可见
                recordUpload.setVisibility(View.GONE);//录制上传图标不可见
            }
        } else if (view.getId() == R.id.iv_record_play_pause) {
            pauseMedia();
        } else if (view.getId() == R.id.iv_record_again) {
            restartRecord();//重新录音
        } else if (view.getId() == R.id.ll_record_view) {  //播放
            if (TextUtils.isEmpty(filePath)) return;
            File file = new File(filePath);
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                return;
            }
            if (file.exists()) {
                startPlay(file);
            }
        }
    }


    public void showMinTimeDialog(String content) {
        CustomPopWindow customPopWindow = new CustomPopWindow(this);
        customPopWindow.setContent(content);
        customPopWindow.setRightGone();
        customPopWindow.setRightButton("我知道了");
        customPopWindow.showPopupWindow();
    }


    public void uploadRecord() {
        if(TextUtils.isEmpty(filePath)){
            toastTip(getString(R.string.picture_choose_fail));
            return;
        }
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.TYPE_AUDIOS, new File(filePath), new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                LogUtils.dTag("阿里oss音频路径onSuccess", mRecordTime + "===" + url);
                p.uploadVoice(url, mRecordTime);
            }

            @Override
            public void onError() {
                LogUtils.dTag("阿里oss音频路径onError", "");
                hideLoadingDialog();
            }
        });
    }


    private void restartRecord() {
        CustomPopWindow customPopWindow = new CustomPopWindow(this);
        customPopWindow.setContent("重新录制会放弃当前录音，\n" +
                "确认重录吗？");
        customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSure() {
                try {//	at com.tftechsz.common.widget.pop.CustomPopWindow.onClick(CustomPopWindow.java:11) 1.7.73
                    File file = new File(filePath);
                    if (file.exists())
                        file.delete();
                    resetRecord();
                } catch (Exception e) {
                    finish();
                }
            }
        });
        customPopWindow.showPopupWindow();

    }


    /**
     * 暂停播放
     */
    private void pauseMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     * 开始播放音频文件
     */
    private void startPlay(File mFile) {
        try {
            if (mediaPlayer == null)
                mediaPlayer = new AudioPlayer(mContext, null, new OnPlayListener() {
                    @Override
                    public void onPrepared() { //开始播放
                        recording.setVisibility(View.GONE);//录制中不可见
                        recordIcon.setVisibility(View.GONE);//录制图标不可见
                        recordPaly.setVisibility(View.GONE);//录制播放图标不可见
                        recordPalyPause.setVisibility(View.VISIBLE);//录制播放暂停图标可见
                        recordAgain.setVisibility(View.VISIBLE);//重新录制图标可见
                        recordUpload.setVisibility(View.VISIBLE);//录制上传图标可见
                        waveLineView.startAnim();
                        voiceImageView.startPlay();
                        recordTip.setText("点击暂停");
                    }

                    @Override
                    public void onCompletion() { //播放完成
                        recording.setVisibility(View.GONE);//录制中不可见
                        recordIcon.setVisibility(View.GONE);//录制图标不可见
                        recordPaly.setVisibility(View.VISIBLE);//录制播放图标可见
                        recordPalyPause.setVisibility(View.GONE);//录制播放暂停图标不可见
                        voiceImageView.stopPlay();
                        mCircleProgressBar.update(0);
                        recordAgain.setVisibility(View.VISIBLE);//重新录制图标可见
                        recordUpload.setVisibility(View.VISIBLE);//录制上传图标可见
                        waveLineView.stopAnim();
                        recordTip.setText("点击播放");
                    }

                    @Override
                    public void onInterrupt() { //播放中断
                        recording.setVisibility(View.GONE);//录制中不可见
                        recordIcon.setVisibility(View.GONE);//录制图标不可见
                        recordPaly.setVisibility(View.VISIBLE);//录制播放图标可见
                        recordPalyPause.setVisibility(View.GONE);//录制播放暂停图标不可见
                        voiceImageView.stopPlay();
                        mCircleProgressBar.update(0);
                        recordAgain.setVisibility(View.VISIBLE);//重新录制图标可见
                        recordUpload.setVisibility(View.VISIBLE);//录制上传图标可见
                        waveLineView.stopAnim();
                        recordTip.setText("点击播放");
                    }

                    @Override
                    public void onError(String error) {
                        waveLineView.stopAnim();
                    }

                    @Override
                    public void onPlaying(long curPosition) {

                    }
                });
            mediaPlayer.setDataSource(mFile.getAbsolutePath());
            mediaPlayer.start(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            e.printStackTrace();
            toastTip("播放错误异常" + e.toString());
        }
    }


    /**
     * 重制状态
     */
    private void resetRecord() {
        recordAgain.setVisibility(View.GONE);
        recordUpload.setVisibility(View.GONE);
        recording.setVisibility(View.GONE);//录制中不可见
        recordIcon.setVisibility(View.VISIBLE);//录制图标可见
        recordPaly.setVisibility(View.GONE);//录制播放图标不可见
        recordPalyPause.setVisibility(View.GONE);//录制播放暂停图标不可见
        recordTip.setText("长按录制");
        recordTime.setText("");
        mCountDownTimer.cancel();
        progress = 0.0f;
        mCircleProgressBar.update(progress);
        llRecordView.setVisibility(View.INVISIBLE);
        waveLineView.stopAnim();//录音波纹停止动画
        voiceImageView.stopPlay();
        stopPlay();
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public boolean showRecordTip(PartyService partyService) {
        boolean isShow = false;
        boolean isOnSeat = MMKVUtils.getInstance().decodeBoolean(Constants.PARTY_IS_ON_SEAT);
        if (partyService.isRunFloatService() && isOnSeat) {
            if (popWindow == null)
                popWindow = new CustomPopWindow(BaseApplication.getInstance());
            popWindow.setContent("在麦位上，需要下麦后，才能进行录音");
            popWindow.setRightButton("我知道了");
            popWindow.setRightGone();
            popWindow.showPopupWindow();
            isShow = true;
        }
        return isShow;
    }

    public void initStartRecord() {
        if (showRecordTip(partyService)) {
            return;
        }
        recording.setVisibility(View.VISIBLE);//录制中可见
        recordIcon.setVisibility(View.GONE);//录制图标不可见
        recordPaly.setVisibility(View.GONE);//录制播放图标不可见
        recordPalyPause.setVisibility(View.GONE);//录制播放暂停图标不可见
        tvRecordMsg.setVisibility(View.VISIBLE);
        recordTime.setBase(SystemClock.elapsedRealtime());
        recordTime.start();
        recordTip.setText("松开结束");
        waveLineView.setVisibility(View.VISIBLE);
        waveLineView.startAnim();//声波可见并动画
        mCircleProgressBar.setVisibility(View.VISIBLE);//录音进度圆圈进度可见
        mCircleProgressBar.setAlpha(1);
        progress = 0.0f;
        mCircleProgressBar.update(progress);
        startRecord();//开始录音
        mCountDownTimer.start();//录音进度比例
    }


    public void startRecord() {
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            mMediaRecorder.setOnInfoListener((mediaRecorder, what, extra) -> {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    recordTime.setText("");
                    //ToastUtils.showLong("录音已经达到最大时间限度20秒");
                    recording.setVisibility(View.GONE);//录制中不可见
                    recordIcon.setVisibility(View.GONE);//录制图标不可见
                    recordPaly.setVisibility(View.VISIBLE);//录制播放图标可见
                    recordPalyPause.setVisibility(View.GONE);//录制播放暂停图标不可见
//                        mCircleProgressBar.setVisibility(View.INVISIBLE);//录音进度圆形圈不可见
                    mCircleProgressBar.update(0);
                    recordAgain.setVisibility(View.VISIBLE);//重新录制图标可见
                    recordUpload.setVisibility(View.VISIBLE);//录制上传图标可见
                    waveLineView.stopAnim();
//                        waveLineView.clearDraw();
                    mCountDownTimer.cancel();
                    tvDuration.setText(TimeUtil.getChronometerSeconds(recordTime) + "''");
                    llRecordView.setVisibility(View.VISIBLE);
                    stopRecord();
                }
            });
            mMediaRecorder.setMaxDuration(MAX_VOICE_TIME);//最长20秒
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            if (filePath == null) {
                filePath = mAudioDir + File.separator + fileName;
            }
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endRecord(int time) {
        mCountDownTimer.cancel();
        llRecordView.setVisibility(View.VISIBLE);
        mCircleProgressBar.update(0);//录音进度圆形圈不可见
        waveLineView.stopAnim();//录音波纹停止动画
        pauseMedia();
        stopRecord();//结束录音
        if (TimeUtil.getChronometerSeconds(recordTime) < time) {
            resetRecord();
            recordTime.stop();
            recordTime.setText("");
            if (time == 5)
                showMinTimeDialog(getString(R.string.mine_record_voice));
            else
                showMinTimeDialog(getString(R.string.mine_accost_record));
            return;
        }
        mRecordTime = TimeUtil.getChronometerSeconds(recordTime);
        tvDuration.setText(mRecordTime + "''");
        recordTime.setText("");
        recordTime.stop();
        recording.setVisibility(View.GONE);//录制中不可见
        recordIcon.setVisibility(View.GONE);//录制图标不可见
        recordPaly.setVisibility(View.VISIBLE);//录制播放图标可见
        recordPalyPause.setVisibility(View.GONE);//录制播放暂停图标不可见
        recordAgain.setVisibility(View.VISIBLE);//重新录制图标可见
        recordUpload.setVisibility(View.VISIBLE);//录制上传图标可见
        tvRecordMsg.setVisibility(View.INVISIBLE);
        mCountDownTimer.cancel();
        llRecordView.setVisibility(View.VISIBLE);
        recordTip.setText("点击播放");
    }


    private void stopRecord() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (Exception e) {
            if (mMediaRecorder != null) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }
    }

    @Override
    public void uploadSuccess(String msg) {
        toastTip("上传音频成功,等待审核通过！");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_AUDIO_SIGN_SUCCESS));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMediaRecorder) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        /**
         * 退出时删除文件
         */
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }

    }

    @Override
    public void addAccostSettingSuccess() {

    }

    @Override
    public void addAccostSettingError() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return false;
    }


}
