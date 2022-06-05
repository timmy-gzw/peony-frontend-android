package com.tftechsz.home.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.tftechsz.home.R;
import com.tftechsz.home.mvp.iview.IRadarView;
import com.tftechsz.home.mvp.presenter.RadarPresenter;
import com.tftechsz.home.widget.RadarView;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.Utils;

import io.reactivex.functions.Consumer;

/**
 * 语音视频速配
 */
public class RadarActivity extends BaseMvpActivity<IRadarView, RadarPresenter> implements RadarView.IScanningListener, View.OnClickListener, IRadarView {

    private final int DELAY_TIME = 10 * 1000;
    public static final String EXTRA_TYPE = "type";
    private RadarView mRadarView;
    private ConstraintLayout mClRadar;  //背景图片
    private ImageView mIvRound, mIvRoundBig;  //圆圈图片
    private int mType;
    private MediaPlayer mediaPlayer;
    private LottieAnimationView mLottie;
    private ImageView mIvAvatar;


    @Autowired
    UserProviderService service;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public static void startActivity(Context context, int value) {
        Intent intent = new Intent(context, RadarActivity.class);
        intent.putExtra(EXTRA_TYPE, value);
        context.startActivity(intent);
    }

    @Override
    public RadarPresenter initPresenter() {
        return new RadarPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.fullScreen(this);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mRadarView = findViewById(R.id.radar_view);
        mClRadar = findViewById(R.id.cl_radar);
        mIvRound = findViewById(R.id.iv_round);
        mIvRoundBig = findViewById(R.id.iv_round_big);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        mLottie = findViewById(R.id.animation_view);
        mLottie.setImageAssetsFolder(Constants.ACCOST_GIFT);//设置data.json引用的图片资源文件夹名称
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_radar;
    }


    @Override
    protected void initData() {
        super.initData();
        mType = getIntent().getIntExtra(EXTRA_TYPE, 1);
        if (mType == 2) {   //视频
            mClRadar.setBackgroundResource(R.drawable.bg_video_radar);
            mIvRound.setBackgroundResource(R.drawable.round_video);
            mIvRoundBig.setBackgroundResource(R.drawable.round_video);
        } else {  //语音
            mClRadar.setBackgroundResource(R.drawable.bg_voice_radar);
            mIvRound.setBackgroundResource(R.drawable.round_voice);
            mIvRoundBig.setBackgroundResource(R.drawable.round_video);
        }
        mRadarView.setScanningListener(this);
        mRadarView.startScan();
//        mRadarView.setImage(service.getUserInfo().getIcon());
        if (service.getUserInfo() != null)
            GlideUtils.loadRouteImage(this, mIvAvatar, service.getUserInfo().getIcon());
        AnimationUtil.createAvatarAnimation(mIvAvatar);
        mRadarView.setMaxScanItemCount(1);
        mLottie.setAnimation("radar.json");//通过AE生成的图文件(json格式)
        Utils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                mLottie.playAnimation();//开始动画
            }
        }, 1000);
        if (mType == 2) {   //视频
            p.videoMatch();
        } else {  //语音
            p.voiceMatch();
        }
        initBus();
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mType == 1)
                p.voiceBeat();
            else
                p.videoBeat();
            handler.postDelayed(this, DELAY_TIME);
        }
    };


    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.<CommonEvent>bindToLifecycle())
                .subscribe(
                        new Consumer<CommonEvent>() {
                            @Override
                            public void accept(CommonEvent event) throws Exception {
                                if (event.type == Constants.NOTIFY_CLOSE_MATCH) {  //语音视频速配最小化关闭页面
                                    finish();
                                }
                            }
                        }
                ));

    }

    @Override
    public void onScanning(int position, float scanAngle) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        start();
        if (mType == 1)
            p.voiceBeat();
        else
            p.videoBeat();
        handler.postDelayed(runnable, DELAY_TIME);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mType == 2) {   //视频
            p.quitVideoMatch();
        } else {  //语音
            p.quitVoiceMatch();
        }
        handler.removeCallbacksAndMessages(null);
        if (runnable != null)
            handler.removeCallbacks(runnable);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void start() {
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer.create(this, R.raw.radar_ring);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onScanSuccess() {
        mRadarView.stopScan();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        }
    }

    @Override
    public void matchSuccess(boolean data) {

    }

    @Override
    public void exitActivity() {
        Utils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);
    }
}
