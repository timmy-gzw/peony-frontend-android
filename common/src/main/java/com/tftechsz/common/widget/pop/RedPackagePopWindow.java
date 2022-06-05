package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private ImageView mIvOpen;
    private final PublicService service;
    private RelativeLayout mRlPrice;  //金额布局
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
        mIvOpen = findViewById(R.id.iv_open);  //开红包
        mIvOpen.setOnClickListener(this);
        mRlPrice = findViewById(R.id.rl_price);
        mTvPrice = findViewById(R.id.tv_price);
        mTvPriceUnit = findViewById(R.id.tv_price_unit);
        mTvTitle = findViewById(R.id.tv_title); // 标题
        TextView tvFrom = findViewById(R.id.tv_form);
        tvFrom.setText(String.format("%s官方红包", mContext.getString(R.string.app_name)));
        findViewById(R.id.iv_close).setOnClickListener(this);
        mTvTitle.setText(redPacket.des);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_open) {
            mIvOpen.setBackgroundResource(R.drawable.anim_red_package);
            if (mIvOpen.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) mIvOpen.getBackground();
                animationDrawable.start();//启动动画
                openRedPacket();
            }
        } else if (id == R.id.iv_close) {
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
                                mIvOpen.setVisibility(View.INVISIBLE);
                                mRlPrice.setVisibility(View.VISIBLE);
                                mTvPrice.setText(response.getData().coin);
                                mTvPriceUnit.setText(response.getData().unit);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        mIvOpen.setVisibility(View.INVISIBLE);
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
