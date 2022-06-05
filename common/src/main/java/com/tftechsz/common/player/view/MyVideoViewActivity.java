package com.tftechsz.common.player.view;

import android.os.Bundle;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.VideoInfo;
import com.tftechsz.common.utils.Utils;

/**
 * 包 名 : com.tftechsz.moment.mvp.ui.activity
 * 描 述 : TODO
 */
public class MyVideoViewActivity extends BaseMvpActivity {
    private VideoView mVideoView;

    @Override
    protected int getLayout() {
        return R.layout.act_trend_video_view;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(this).fullScreen(true)
                .hideBar(BarHide.FLAG_HIDE_BAR)
                .init();
        VideoInfo info = (VideoInfo) getIntent().getSerializableExtra(Interfaces.EXTRA_VIDEO_INFO);
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setOnClickListener(v -> {
            Utils.finishAfterTransition(mActivity);
        });
        if (info != null) {
            if (info.isVideo_local()) {
                mVideoView.setUrl(info.getVideo_url());
            } else {
                String proxyUrl = BaseApplication.getProxy(mContext).getProxyUrl(info.getVideo_url());
                mVideoView.setUrl(proxyUrl);
            }
            mVideoView.start();
        }
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release();
    }
}
