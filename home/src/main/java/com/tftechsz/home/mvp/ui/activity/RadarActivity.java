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
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.MatchPopWindow;
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

    private final int DELAY_TIME = 10 * 1000,TEXT_ANIMATION_TIME = 400,MATCH_INTERVAL_TIME = 1000;
    public static final String EXTRA_TYPE = "type";
    private ImageView mRadarView,mScaleImageView1,mScaleImageView2,mScaleAlphaImageView1,mScaleAlphaImageView2,mScaleAlphaImageView3;
    private ConstraintLayout mClRadar;  //背景图片
    private ImageView mIvRound, mIvRoundBig;  //圆圈图片
    private int mType;
    private MediaPlayer mediaPlayer;
    private ImageView mIvAvatar,mMusicImageView,mRuleImageView;
    private TextView mTitleTv;
    private BarrageView mBarrageView;
    private TextView mTvTip,mTvMatching,mTvMatchInterval;
    private LinearLayout mllPair;
    private MatchPopWindow matchPopWindow;
    private boolean isFirst = true;

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
        mTvMatching = findViewById(R.id.tv_matching);
        mTvMatchInterval = findViewById(R.id.tv_match_interval);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_radar;
    }

    private boolean needShowPop() {
            long lastShowMomentTime = MMKVUtils.getInstance().decodeLong(Constants.LAST_SHOW_MATCH_POP_TIME);//上次显示动态引导页的时间戳ms
            long l = System.currentTimeMillis();
            boolean sameDay = TimeUtil.isSameDay(lastShowMomentTime, l);
            if (l > lastShowMomentTime && !sameDay) {
                showPopWindow();
                MMKVUtils.getInstance().encode(Constants.LAST_SHOW_MATCH_POP_TIME, l);
                return true;
            }
            return false;
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
        initBus();
    }

    private void startBarrage() {
        List<Barrage> mBarrages = new ArrayList<>();
        String[] datas = {"衣衣 和 雨啊 速配成功","阿里 和 深爱的 速配成功","东方 和 爱笑的机器狗 速配成功","阿苏妲 和 马小龙 速配成功","小迷糊 和 雨啊 速配成功"};
        for (int i = 0; i < datas.length; i++) {
            mBarrages.add(new Barrage(datas[i],getResources().getColor(R.color.white)));
        }
        mBarrageView.setBarrages(mBarrages);
    }

    private void stopBarrage(){
        mBarrageView.setBarrages(new ArrayList<>());
    }


    private void startAnimation() {
        startBarrage();
        handler.postDelayed(run,200);
//        AnimationUtil.createAvatarAnimation(mIvAvatar);
        AnimationUtil.createRotateRevertAnimation(mRadarView,2500);
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
        stopBarrage();
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

    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            int len = mTvMatching.length();
            if(len == 3){
                mTvMatching.setText(".");
            }else if(len == 2){
                mTvMatching.setText("...");
            }else{
                mTvMatching.setText("..");
            }
            handler.postDelayed(this, TEXT_ANIMATION_TIME);
        }
    };

    private int interval = 3;
    private final Runnable runInterval = new Runnable() {
        @Override
        public void run() {
            if(interval == 1){
                interval = 3;
                mTvMatchInterval.setText("（"+ interval +"S后自动参与速配）");
                mTvTip.setVisibility(View.VISIBLE);
                mTvMatching.setVisibility(View.VISIBLE);
                mllPair.setVisibility(View.GONE);
                startMatch();
            }else{
                interval --;
                mTvMatchInterval.setText("（"+ interval +"S后自动参与速配）");
                handler.postDelayed(this, MATCH_INTERVAL_TIME);
            }

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
        if(isFirst){
            isFirst = false;
            if(!needShowPop()){
                startMatch();
            }
        }else{
            handler.postDelayed(runInterval, MATCH_INTERVAL_TIME);
        }
    }

    private void startMatch() {
        startAnimation();
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
        stopMatch();
        mTvTip.setVisibility(View.GONE);
        mTvMatching.setVisibility(View.GONE);
        mllPair.setVisibility(View.VISIBLE);
        if(null != matchPopWindow)
            matchPopWindow.dismiss();
    }

    private void stopMatch() {
        clearAnimation();
        if (mType == 2) {   //视频
            p.quitVideoMatch();
        } else {  //语音
            p.quitVoiceMatch();
        }
        handler.removeCallbacksAndMessages(null);
        if (runnable != null)
            handler.removeCallbacks(runnable);
        if (run != null)
            handler.removeCallbacks(run);
        if (runInterval != null)
            handler.removeCallbacks(runInterval);
        release();
    }

    private void release(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mMusicImageView.clearAnimation();
        }
    }


    private void start() {
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer.create(this, R.raw.radar_ring);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            AnimationUtil.createRotateRevertAnimation(mMusicImageView,1500);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBarrageView.destroy();
    }

    @Override
    public void onScanSuccess() {

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
                mMediaIsClose = true;
                mMusicImageView.setImageResource(R.mipmap.radar_musice_icon_un);
            }else {
                start();
                mMediaIsClose = false;
                mMusicImageView.setImageResource(R.mipmap.radar_music_icon);
            }
        }
        if(id == R.id.rule_im){
            //匹配规则
            stopMatch();
            showPopWindow();
        }
        if(id == R.id.ll_pair){
            mTvTip.setVisibility(View.VISIBLE);
            mTvMatching.setVisibility(View.VISIBLE);
            mllPair.setVisibility(View.GONE);
            startMatch();
        }
    }

    private void showPopWindow() {
        if(null == matchPopWindow)
            matchPopWindow = new MatchPopWindow(this, v -> finish());
        matchPopWindow
                .setTitle(mType==2?"视频速配规则":"语音速配规则")
                .setContent(mType==2?"1、 视频速配能帮您快速匹配有缘人，使用过程中需提高自我保护意识；\n2、 严禁谩骂，涉黄，广告，讨论敏感话题等行为，若遇不良体验请立即挂断并举报。":
                        "1、 语音速配能帮您快速匹配有缘人，使用过程中需提高自我保护意识；\n2、 严禁谩骂，涉黄，广告，讨论敏感话题等行为，若遇不良体验请立即挂断并举报。")
                .onSureClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startMatch();
                    }
                })
                .showPopupWindow();
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
