package com.tftechsz.family.widget.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.tftechsz.family.R;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyRecruitDto;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 包 名 : com.tftechsz.family.widget.pop
 * 描 述 : 家族招募红包弹窗
 */
public class FamilyRecruitPop extends BaseCenterPop implements View.OnClickListener {

    private ImageView mIvOpen;
    private AnimationDrawable mAnimationDrawable;
    private ImageView mIcon;
    private TextView mName;
    private TextView mDesc;
    private TextView mPrice;
    private int red_id;
    private long tid;
    private FamilyApiService service;
    protected CompositeDisposable mCompositeDisposable;
    private OpenRedPacketListener mListener;

    public FamilyRecruitPop(Context context, OpenRedPacketListener mListener) {
        super(context);
        init();
        setOutSideDismiss(false);
        this.mListener = mListener;
    }

    private void init() {
        mCompositeDisposable = new CompositeDisposable();
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_open).setOnClickListener(this);
        mIvOpen = findViewById(R.id.iv_open);
        mIcon = findViewById(R.id.icon);
        mName = findViewById(R.id.form_name);
        mDesc = findViewById(R.id.tv_title);
        mPrice = findViewById(R.id.price);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.stop();
                    mIvOpen.setBackgroundResource(com.tftechsz.common.R.mipmap.ic_open_red_package);
                }
            }
        });
    }

    public void setItem(FamilyRecruitDto item) {
        red_id = item.id;
        this.tid = item.tid;
        GlideUtils.loadImage(mContext, mIcon, item.icon);
        mName.setText(item.tname);
        mDesc.setText(item.des);
        mPrice.setText(String.valueOf(item.coin));
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_recruit);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) { //关闭弹窗
            dismiss();
        } else if (id == R.id.iv_open) { //开红包
            if (!NetworkUtils.isConnected()) {//无网络吐司
                Utils.toast(R.string.net_error);
                return;
            }
            mIvOpen.setBackgroundResource(com.tftechsz.common.R.drawable.anim_red_package);
            if (mIvOpen.getBackground() instanceof AnimationDrawable) {
                mAnimationDrawable = (AnimationDrawable) mIvOpen.getBackground();
                if (!mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.start();
                    openRedPacket();
                }
            }
        }
    }

    //打开红包
    private void openRedPacket() {
        mCompositeDisposable.add(service.openRedPacket(red_id).compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        Utils.runOnUiThreadDelayed(() -> {
                            Utils.toast("领取成功!");

                            SPUtils.getInstance().put(Interfaces.SP_TEAM_ID, String.valueOf(tid));
                            CommonUtil.startTeamChatActivity((Activity) mContext,String.valueOf(tid) + "");
                            dismiss();
                            mListener.success();
                        }, 500);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        dismiss();
                    }
                }));
    }

    public interface OpenRedPacketListener {
        void success();
    }

}
