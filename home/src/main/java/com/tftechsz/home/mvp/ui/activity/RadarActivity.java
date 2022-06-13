package com.tftechsz.home.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.netease.nim.uikit.common.adapter.AdvancedAdapter;
import com.tftechsz.home.R;
import com.tftechsz.home.mvp.iview.IRadarView;
import com.tftechsz.home.mvp.presenter.RadarPresenter;
import com.tftechsz.home.widget.BarrageView.Barrage;
import com.tftechsz.home.widget.BarrageView.BarrageView;
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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 语音视频速配
 */
public class RadarActivity extends BaseMvpActivity<IRadarView, RadarPresenter> implements RadarView.IScanningListener, View.OnClickListener, IRadarView {

    private final int DELAY_TIME = 10 * 1000;
    public static final String EXTRA_TYPE = "type";
    private ImageView mRadarView,mScaleImageView1,mScaleImageView2,mScaleAlphaImageView1,mScaleAlphaImageView2,mScaleAlphaImageView3;
    private ConstraintLayout mClRadar;  //背景图片
    private ImageView mIvRound, mIvRoundBig;  //圆圈图片
    private int mType;
    private MediaPlayer mediaPlayer;
    private ImageView mIvAvatar,mMusicImageView,mRuleImageView;
    private TextView mTitleTv;
    private BarrageView mBarrageView;
    private TextView mTvTip;
    private LinearLayout mllPair;


    @Autowired
    UserProviderService service;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean mMediaIsClose = false;

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
        mScaleImageView1 = findViewById(R.id.scale_im1);
        mScaleImageView2 = findViewById(R.id.scale_im2);
        mTitleTv = findViewById(R.id.title_tv);
        mBarrageView = findViewById(R.id.barrage_view);
        mScaleAlphaImageView1 = findViewById(R.id.sa_im1);
        mScaleAlphaImageView2 = findViewById(R.id.sa_im2);
        mScaleAlphaImageView3 = findViewById(R.id.sa_im3);
        mMusicImageView = findViewById(R.id.music_iv);
        mTvTip = findViewById(R.id.tv_tip);
        mllPair = findViewById(R.id.ll_pair);
        mllPair.setOnClickListener(this);
        findViewById(R.id.rule_im).setOnClickListener(this);
        mMusicImageView.setOnClickListener(this);
        mClRadar = findViewById(R.id.cl_radar);
        mIvRound = findViewById(R.id.iv_round);
        mIvRoundBig = findViewById(R.id.iv_round_big);
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
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
            mTitleTv.setText("视频速配");
            mClRadar.setBackgroundResource(R.mipmap.radar_video_bg);
            mIvRound.setBackgroundResource(R.drawable.round_video);
            mIvRoundBig.setBackgroundResource(R.drawable.round_video);
        } else {  //语音
            mClRadar.setBackgroundResource(R.mipmap.radar_voice_bg);
            mIvRound.setBackgroundResource(R.drawable.round_voice);
            mIvRoundBig.setBackgroundResource(R.drawable.round_video);
        }
        if (service.getUserInfo() != null)
            GlideUtils.loadRouteImage(this, mIvAvatar, service.getUserInfo().getIcon());
        startAnimation();
        if (mType == 2) {   //视频˛˛
            p.videoMatch();
        } else {  //语音
            p.voiceMatch();
        }
        initBus();
        List<Barrage> mBarrages = new ArrayList<>();
        String[] datas = {"衣衣 和 雨啊 速配成功","阿里 和 深爱的 速配成功","东方 和 爱笑的机器狗 速配成功","阿苏妲 和 马小龙 速配成功","小迷糊 和 雨啊 速配成功"};
        for (int i = 0; i < datas.length; i++) {
            mBarrages.add(new Barrage(datas[i],getResources().getColor(R.color.white)));
        }
        mBarrageView.setBarrages(mBarrages);
    }

    private void startAnimation() {
        AnimationUtil.createAvatarAnimation(mIvAvatar);
        AnimationUtil.createRotateRevertAnimation(mRadarView);
        AnimationUtil.createScaleAnimation(mScaleImageView1);
        AnimationUtil.createScaleAlphaAnimation(mScaleAlphaImageView1);
        Utils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.createScaleAnimation(mScaleImageView2);
                AnimationUtil.createScaleAlphaAnimation(mScaleAlphaImageView2);
            }
        }, 1000);
        Utils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.createScaleAlphaAnimation(mScaleAlphaImageView3);
            }
        }, 700);
    }

    private void clearAnimation(){
        mIvAvatar.clearAnimation();
        mRadarView.clearAnimation();
        mScaleImageView1.clearAnimation();
        mScaleAlphaImageView1.clearAnimation();
        mScaleImageView2.clearAnimation();
        mScaleAlphaImageView2.clearAnimation();
        mScaleAlphaImageView3.clearAnimation();
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
        if(!mMediaIsClose) {
            start();
        }
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
        release();
        mMediaIsClose = false;
    }
    
    private void release(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mMediaIsClose = true;
        }
    }


    private void start() {
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer.create(this, R.raw.radar_ring);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            mMediaIsClose = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBarrageView.destroy();
    }

    @Override
    public void onScanSuccess() {
        clearAnimation();
        mTvTip.setVisibility(View.GONE);
        mllPair.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        }
        if(id == R.id.music_iv){
            if(!mMediaIsClose){
                release();
                mMusicImageView.setImageResource(R.mipmap.radar_musice_icon_un);
            }else {
                start();
                mMusicImageView.setImageResource(R.mipmap.radar_music_icon);
            }
        }
        if(id == R.id.rule_im){
            //匹配规则‰
        }
        if(id == R.id.ll_pair){
            mTvTip.setVisibility(View.VISIBLE);
            mllPair.setVisibility(View.GONE);
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
