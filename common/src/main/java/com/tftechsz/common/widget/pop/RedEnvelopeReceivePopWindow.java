package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.RedDetailInfo;
import com.tftechsz.common.entity.RedPacketDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;

import io.reactivex.disposables.CompositeDisposable;

/**
 *  红包领取弹窗
 */
public class RedEnvelopeReceivePopWindow extends BaseCenterPop implements View.OnClickListener {

    private int red_packet_id;
    private RedDetailInfo info;
    private ImageView mIvIcon, mIvOpen;
    private final PublicService service;
    private TextView mTvName, mTvTitle;
    protected CompositeDisposable mCompositeDisposable;
    private TextView mReceiveDetail;
    private Context mContext;
    UserProviderService mUserProviderService;
    private AnimationDrawable mAnimationDrawable;
    private RedEnvelopeDetailsPopWindow mDetailsPopWindow;
    private RedEnvelopeDetailsPopWindow mDetailsPopWindow1;
    private RedEnvelopeTypeListener listener;

    public RedEnvelopeReceivePopWindow(Context context, RedEnvelopeTypeListener listener) {
        super(context);
        this.mContext = context;
        service = RetrofitManager.getInstance().createExchApi(PublicService.class);
        mUserProviderService = ARouter.getInstance().navigation(UserProviderService.class);
        mCompositeDisposable = new CompositeDisposable();
        setRedEnvelopeTypeListener(listener);
        setOutSideDismiss(false);
        initUI();
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_red_envelope_receive);
    }

    private void initUI() {
        mIvIcon = findViewById(R.id.icon);
        mTvName = findViewById(R.id.form_name);
        mTvTitle = findViewById(R.id.tv_title); // 标题
        mIvOpen = findViewById(R.id.iv_open);  //开红包
        mReceiveDetail = findViewById(R.id.receive_detail); //领取详情
        mIvOpen.setOnClickListener(this);
        mReceiveDetail.setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mIvOpen.setBackgroundResource(R.mipmap.ic_open_red_package);
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.stop();
                }
            }
        });
    }

    public void showPopup(int red_packet_id, RedDetailInfo info) {
        if (isShowing()) {
            return;
        }
        this.red_packet_id = red_packet_id;
        this.info = info;
        initData();
        showPopupWindow();
    }

    private void initData() {
        GlideUtils.loadRoundImage(mContext, mIvIcon, info.icon, 3);
        mTvName.setText(info.title);
        mTvTitle.setText(info.des);

        if (mUserProviderService.getUserId() == info.create_red_packet_user_id) {//如果是自己发的红包才显示领取详情
            mReceiveDetail.setVisibility(View.VISIBLE);
        } else {
            mReceiveDetail.setVisibility(View.INVISIBLE);
        }

        if (info.is_complete == 1) {
            mTvTitle.setText(Interfaces.REDPACKAGE_FINISHED);
            mIvOpen.setVisibility(View.INVISIBLE);
            mReceiveDetail.setVisibility(View.VISIBLE);
            return;
        }
        if (info.is_expire == 1) {
            mTvTitle.setText(Interfaces.REDPACKAGE_EXPIRED);
            mIvOpen.setVisibility(View.INVISIBLE);
            mReceiveDetail.setVisibility(View.VISIBLE);
            return;
        }
        mIvOpen.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_open) {
            mIvOpen.setBackgroundResource(R.drawable.anim_red_package);
            if (mIvOpen.getBackground() instanceof AnimationDrawable) {
                mAnimationDrawable = (AnimationDrawable) mIvOpen.getBackground();
                if (mAnimationDrawable.isRunning()) {
                    return;
                }
                mAnimationDrawable.start();//启动动画
                openRedPacket();
            }
        } else if (id == R.id.receive_detail) {
            dismiss();
            if (mDetailsPopWindow1 == null) {
                mDetailsPopWindow1 = new RedEnvelopeDetailsPopWindow(mContext);
            }
            mDetailsPopWindow1.showPopup(red_packet_id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable == null) return;
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
    }

    /**
     * 开红包
     */
    public void openRedPacket() {
        mCompositeDisposable.add(service.openRedPacket(red_packet_id).compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RedPacketDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RedPacketDto> response) {
                        Utils.runOnUiThreadDelayed(() -> {
                            dismiss();
                            listener.redType(1);
                            if (mDetailsPopWindow == null) {
                                mDetailsPopWindow = new RedEnvelopeDetailsPopWindow(mContext);
                            }
                            mDetailsPopWindow.showPopup(red_packet_id);
                        }, 100);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if (code == 1002) {
                            listener.redType(2);
                            mTvTitle.setText(Interfaces.REDPACKAGE_FINISHED);
                        } else if (code == 1003) {
                            listener.redType(3);
                            mTvTitle.setText(Interfaces.REDPACKAGE_EXPIRED);
                        } else {
                            super.onFail(code, msg);
                        }
                        mTvTitle.setText(msg);
                        mIvOpen.setVisibility(View.INVISIBLE);
                        mReceiveDetail.setVisibility(View.VISIBLE);
                        mIvOpen.setBackgroundResource(R.mipmap.ic_open_red_package);
                        mAnimationDrawable.stop();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mIvOpen.setBackgroundResource(R.mipmap.ic_open_red_package);
                        mAnimationDrawable.stop();
                    }
                }));
    }

    private void setRedEnvelopeTypeListener(RedEnvelopeTypeListener listener) {
        this.listener = listener;
    }

    public interface RedEnvelopeTypeListener {
        void redType(int type);  //红包类型  1 已领取   2已领完  3已过期
    }
}
