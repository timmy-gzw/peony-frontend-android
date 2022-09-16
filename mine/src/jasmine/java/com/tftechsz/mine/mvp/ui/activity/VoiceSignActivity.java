package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.FileUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.presenter.VoiceSignPresenter;

import java.io.File;

@Route(path = ARouterApi.ACTIVITY_VOICESIGN)
public class VoiceSignActivity extends BaseVoiceRecordActivity {


    @Override
    protected int getLayout() {
        return R.layout.activity_voice_sign;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).build();
        voiceImageView = findViewById(R.id.iv_voice);
        mCircleProgressBar = findViewById(R.id.circleProgressBar);
        recordTime = findViewById(R.id.record_time);
        recordTip = findViewById(R.id.id_record_info);
        recordIcon = findViewById(R.id.iv_record_icon);
        recording = findViewById(R.id.iv_record_ing);
        recordPaly = findViewById(R.id.iv_record_play);
        recordPalyPause = findViewById(R.id.iv_record_play_pause);
        recordUpload = findViewById(R.id.iv_record_upload);
        recordAgain = findViewById(R.id.iv_record_again);
        tvRecordMsg = findViewById(R.id.tv_record_msg);
        tvDuration = findViewById(R.id.tv_duration);
        llRecordView = findViewById(R.id.ll_record_view);
        llRecordView.setOnClickListener(this);
        waveLineView = findViewById(R.id.waveLineView);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("语音签名");
        waveLineView.setLineColor(ColorUtils.getColor(R.color.colorPrimary));
        waveLineView.setSensibility(3);
        waveLineView.setVolume(60);
        recordPaly.setOnClickListener(this);
        recordPalyPause.setOnClickListener(this);
        recordUpload.setOnClickListener(this);
        recordAgain.setOnClickListener(this);
        recordIcon.setOnTouchListener((v, event) -> {
            final String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            PermissionUtil.beforeCheckPermission(this,event, permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions(VoiceSignActivity.this)
                            .request(permissions)
                            .subscribe(grant -> {
                                if (grant) {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        touched = true;
                                        initStartRecord();
                                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                                            || event.getAction() == MotionEvent.ACTION_UP) {
                                        if (touched) {
                                            endRecord(5);
                                        }
                                    }
                                } else {
                                    toastTip("请允许录音权限");
                                }
                            }));
                } else {
                    toastTip("请允许录音权限");
                }
            });
            return true;
        });
        mAudioDir = new File(getCacheDir(), AUDIO_DIR_NAME);
        FileUtils.createOrExistsDir(mAudioDir);
        setMaxTime(20 * 1000, 5, 1.9f);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.iv_record_upload) {
            if (mRecordTime >= 5) {
                GlobalDialogManager.getInstance().show(getFragmentManager(), "正在上传音频");
                uploadRecord();  //上传录音
            } else {
                showMinTimeDialog(getString(R.string.mine_accost_record));
            }
        }
    }


    @Override
    protected void initData() {
        super.initData();
        setTitle("语音签名");
    }


    @Override
    public VoiceSignPresenter initPresenter() {
        return new VoiceSignPresenter();
    }


}

