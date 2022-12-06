package com.tftechsz.common.widget.pop;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.RedPacketDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;

import io.reactivex.disposables.CompositeDisposable;

/**
 *  红包弹窗
 */
public class RedPackagePopWindow extends BaseCenterPop implements View.OnClickListener {

    private final ChatMsg.RedPacket redPacket;
    private RelativeLayout mIvOpen,rlBg;
    private final PublicService service;
    private ConstraintLayout mClPrice;  //金额布局
    private TextView mTvPrice, mTvPriceUnit; //金额
    private TextView mTvTitle;
    protected CompositeDisposable mCompositeDisposable;


    public RedPackagePopWindow(Context context, ChatMsg.RedPacket redPacket) {
        super(context);
        this.redPacket = redPacket;
        service = RetrofitManager.getInstance().createExchApi(PublicService.class);
        mCompositeDisposable = new CompositeDisposable();
        setOutSideDismiss(false);
        initUI();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_red_package);
    }

    private void initUI() {
        RelativeLayout rlRed = findViewById(R.id.rl_red);
        RelativeLayout rlCard = findViewById(R.id.rl_card);
        mIvOpen = findViewById(R.id.rl_red);  //开红包
        mIvOpen.setOnClickListener(this);
        mClPrice = findViewById(R.id.cl_price);
        mTvPrice = findViewById(R.id.tv_price);
        rlBg = findViewById(R.id.rl_bg);
        mTvPriceUnit = findViewById(R.id.tv_price_unit);
        mTvTitle = findViewById(R.id.tv_title); // 标题
        TextView tvFrom = findViewById(R.id.tv_form);
        tvFrom.setText(String.format("%s官方红包", mContext.getString(R.string.app_name)));
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.tv_know).setOnClickListener(this);
        mTvTitle.setText(redPacket.des);
        setCameraDistance();

        if(TextUtils.equals("chat_cart",redPacket.type)){
            TextView tvTitle1 = findViewById(R.id.tv_title1);
            tvTitle1.setText(redPacket.des);
            TextView tvContent = findViewById(R.id.tv_desc);
            tvContent.setText(redPacket.desc);
            rlRed.setVisibility(View.GONE);
            rlCard.setVisibility(View.VISIBLE);
        }else {
            rlRed.setVisibility(View.VISIBLE);
            rlCard.setVisibility(View.GONE);
        }
    }

    // 改变视角距离, 贴近屏幕,这个必须设置，因为如果不这么做，沿着Y轴旋转的过程中有可能产生超出屏幕的3D效果。
    private void setCameraDistance() {
        int distance = 16000;
        float scale = mContext.getResources().getDisplayMetrics().density * distance;
        mIvOpen.setCameraDistance(scale);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_red) {
            mIvOpen.setClickable(false);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mIvOpen,"rotationY",0.0f,90f);
            animator.setDuration(300);
            animator.setInterpolator(new LinearInterpolator());
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mTvTitle.setVisibility(View.GONE);
                    mClPrice.setVisibility(View.VISIBLE);
                    rlBg.setBackgroundResource(R.mipmap.bg_red_package_jasmine_open);
                    mIvOpen.setRotationY(-270f);
                    ObjectAnimator animator2 = ObjectAnimator.ofFloat(mIvOpen,"rotationY",270.0f,360f);
                    animator2.setDuration(300);
                    animator2.setInterpolator(new LinearInterpolator());
                    animator2.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
            openRedPacket();
        } else if (id == R.id.iv_close || id == R.id.tv_know) {
            if (listener != null && redPacket != null && TextUtils.equals(redPacket.scene, "task_register_new_user"))
                listener.onCancel();
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    /**
     * 开红包
     */
    public void openRedPacket() {
        if (redPacket == null) {
            return;
        }
        mCompositeDisposable.add(service.openRedPacket(redPacket.red_packet_id).compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RedPacketDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RedPacketDto> response) {
                        Utils.runOnUiThreadDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mTvTitle.setVisibility(View.GONE);
                                mClPrice.setVisibility(View.VISIBLE);
                                rlBg.setBackgroundResource(R.mipmap.bg_red_package_jasmine_open);
                                mTvPrice.setText(response.getData().coin);
                                mTvPriceUnit.setText(response.getData().unit);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));

    }


    public interface OnSelectListener {

        void onCancel();

        void onSure();

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
