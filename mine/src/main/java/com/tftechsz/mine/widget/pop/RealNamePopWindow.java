package com.tftechsz.mine.widget.pop;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.R;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;

/**
 *  兑换详情弹窗
 */
public class RealNamePopWindow extends BaseCenterPop implements View.OnClickListener {

    private int mType;
    private ConfigInfo configInfo;
    UserProviderService service;
    private Activity mActivity;


    public RealNamePopWindow(Activity context, int type) {
        super(context);
        this.mActivity = context;
        this.mType = type;
        initUI();
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_real_name);
    }

    private void initUI() {
        setOutSideDismiss(false);
        TextView mTvTip = findViewById(R.id.tv_tip);
        mTvTip.setOnClickListener(this);
        findViewById(R.id.tv_real_name).setOnClickListener(this);   // 去认证
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.iv_cancel).setOnClickListener(this);
        mTvTip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//画线
        mTvTip.getPaint().setAntiAlias(true);//抗锯齿
        TextView tvTitle = findViewById(R.id.tv_title);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        configInfo = service.getConfigInfo();
        if (mType == 1) {   //真人认证
            tvTitle.setText("您还未进行真人认证，真人认证才可以发起提现哦～");
            mTvTip.setText("真人认证不了怎么办？");
            if (null != configInfo.api && null != configInfo.api.integral_exchange_how_to_real_1) {
                mTvTip.setVisibility(View.VISIBLE);
            } else {
                mTvTip.setVisibility(View.GONE);
            }
        } else {
            tvTitle.setText("您还未进行实名认证，实名认证后才可以发起提现哦～");
            mTvTip.setText("实名认证不了怎么办？");
            if (null != configInfo.api && null != configInfo.api.integral_exchange_how_to_real_2) {
                mTvTip.setVisibility(View.VISIBLE);
            } else {
                mTvTip.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_cancel || id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_real_name) {
            if (listener != null)
                listener.onRealName();
            dismiss();
        } else if (id == R.id.tv_tip) {
            if (mType == 1) {   //真人认证
                if (null != configInfo.api && null != configInfo.api.integral_exchange_how_to_real_1) {
                    CommonUtil.performLink(mActivity, configInfo.api.integral_exchange_how_to_real_1, 0, 20);
                }
            } else {
                if (null != configInfo.api && null != configInfo.api.integral_exchange_how_to_real_2) {
                    CommonUtil.performLink(mActivity, configInfo.api.integral_exchange_how_to_real_2, 0, 20);
                }
            }

        }
    }


    public interface OnSelectListener {
        void onRealName();

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
