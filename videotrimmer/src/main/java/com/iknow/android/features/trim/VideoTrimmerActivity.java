package com.iknow.android.features.trim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.iknow.android.R;
import com.iknow.android.databinding.ActivityVideoTrimBinding;
import com.iknow.android.features.common.ui.IBaseActivity;
import com.iknow.android.interfaces.VideoTrimListener;
import com.iknow.android.utils.ToastUtil;
import com.iknow.android.utils.UIThreadUtil;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.constant.Interfaces;

import androidx.databinding.DataBindingUtil;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
@Route(path = ARouterApi.ACTIVITY_VIDEOTRIMMER)
public class VideoTrimmerActivity extends IBaseActivity implements VideoTrimListener {

    private ActivityVideoTrimBinding mBinding;
    private ProgressDialog mProgressDialog;

    @Override
    public void initUI() {
        ImmersionBar.with(this).barColor(R.color.black).init();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_trim);
        Bundle bd = getIntent().getExtras();
        String path = "";
        if (bd != null) path = bd.getString(Interfaces.VIDEO_PATH_KEY);
        if (mBinding.trimmerView != null) {
            mBinding.trimmerView.setOnTrimVideoListener(this);
            mBinding.trimmerView.initVideoByURI(Uri.parse(path));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.trimmerView.onVideoPause();
        mBinding.trimmerView.setRestoreState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.trimmerView.onDestroy();
    }

    @Override
    public void onStartTrim() {
        UIThreadUtil.runOnUiThread(() -> buildDialog(getResources().getString(R.string.trimming)).show());
    }

    @Override
    public void onFinishTrim(String in, int video_len) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
        ToastUtil.show(this, getString(R.string.trimmed_done));
        Intent intent = new Intent();
        intent.putExtra(Interfaces.EXTRA_TRIM_VIDEO_PATH, in);
        intent.putExtra(Interfaces.EXTRA_TRIM_VIDEO_LENGHT, video_len);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCancel() {
        mBinding.trimmerView.onDestroy();
        finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mContext, "", msg);
            mProgressDialog.setMessage(msg);
        }
        return mProgressDialog;
    }
}
