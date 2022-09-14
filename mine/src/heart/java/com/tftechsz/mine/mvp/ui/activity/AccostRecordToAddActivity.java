package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.AccostSettingListBean;

import java.io.File;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui0.activity
 * 描 述 : 我的语音 添加
 */
public class AccostRecordToAddActivity extends BaseVoiceRecordActivity {

    private int mAccostSize;  //语音招呼数量
    private int mStatus;   //0 添加  1 更新

    @Override
    protected int getLayout() {
        return R.layout.act_accost_record_add;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).transparentStatusBar().statusBarDarkFont(false, 0.2f).init();
        new ToolBarBuilder().showBack(true).setTitle("语音招呼设置")
                .setRightText(Interfaces.ACCOST_RECORD_TOMY, v -> finish())
                .build();
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
        waveLineView.setLineColor(ColorUtils.getColor(R.color.driver));
        waveLineView.setSensibility(3);
        waveLineView.setVolume(60);
        recordPaly.setOnClickListener(this);
        recordPalyPause.setOnClickListener(this);
        recordUpload.setOnClickListener(this);
        recordAgain.setOnClickListener(this);
        recordIcon.setOnTouchListener((v, event) -> {
            final String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            PermissionUtil.beforeCheckPermission(this, permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions(AccostRecordToAddActivity.this)
                            .request(permissions)
                            .subscribe(grant -> {
                                if (grant) {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        touched = true;
                                        initStartRecord();
                                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                                            || event.getAction() == MotionEvent.ACTION_UP) {
                                        if (touched) {
                                            endRecord(2);
                                        }
                                    }
                                } else {
                                    toastTip("请允许录音权限");
                                }
                            }));
                }
            });
            return true;
        });
        mAudioDir = new File(getCacheDir(), AUDIO_DIR_NAME);
        FileUtils.createOrExistsDir(mAudioDir);
        setMaxTime(8 * 1000, 2, 4.5f);
    }

    @Override
    protected void initData() {
        super.initData();
        tvRecordMsg.setText("录制一段2~8秒的声音");
        mAccostSize = getIntent().getIntExtra("accostSize", mAccostSize);
        mStatus = getIntent().getIntExtra("status", 0);
    }


    public void uploadAccostRecord() {
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
                if (mStatus == 0) {
                    p.addAccostSetting(new AccostSettingListBean(url, "搭讪语音" + (mAccostSize + 1), mRecordTime));
                }
            }

            @Override
            public void onError() {
                LogUtils.dTag("阿里oss音频路径onError", "");
                hideLoadingDialog();
            }
        });
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.iv_record_upload) {
            if (!ClickUtil.canOperate()) return;
            if (TextUtils.isEmpty(filePath)) {
                toastTip("请录制后上传");
                return;
            }
            if (mRecordTime >= 2) {
                GlobalDialogManager.getInstance().show(getFragmentManager(), "正在上传音频");
                uploadAccostRecord();  //上传录音
            } else {
                showMinTimeDialog(getString(R.string.mine_accost_record));
            }
        }
    }


    @Override
    public void addAccostSettingSuccess() {
        super.addAccostSettingSuccess();
        Intent intent = new Intent();
        intent.putExtra("status", mStatus);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void addAccostSettingError() {
        super.addAccostSettingError();
        hideLoadingDialog();
    }
}
