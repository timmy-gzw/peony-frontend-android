package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.mine.R;

/**
 * 实名认证提交成功
 */
@Route(path = ARouterApi.ACTIVITY_REAL_SUCCESS)
public class RealNameSuccessActivity extends BaseMvpActivity implements View.OnClickListener {
    private int mStatus;  // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
    private ImageView mIvStatus;
    private TextView mTvStatus, mTvTip;
    private TextView mTvBackHome;
    private View mIvFail;
    private String mErrorMsg;
    private boolean isInPartySelf;

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .build();
        mIvStatus = findViewById(R.id.iv_status);
        mTvStatus = findViewById(R.id.tv_status);
        mTvTip = findViewById(R.id.tv_tip);
        mIvFail = findViewById(R.id.iv_tip_status);
        mTvBackHome = findViewById(R.id.tv_back_home);
        initListener();

    }

    private void initListener() {
        mTvBackHome.setOnClickListener(this);   //返回首页
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_real_name_success;
    }

    @Override
    protected void initData() {
        super.initData();
        mStatus = getIntent().getIntExtra(Constants.EXTRA_STATUS, 0);
        mErrorMsg = getIntent().getStringExtra(Interfaces.EXTRA_MESSAGE);
        isInPartySelf = getIntent().getBooleanExtra(Interfaces.EXTRA_TYPE, false);
        setData();
    }

    private void setData() {
        if (mStatus == 0) {   //审核中
            mIvStatus.setBackgroundResource(R.mipmap.mine_ic_real_name_doing);
            mTvStatus.setText("正在审核中");
            mTvTip.setText("工作人员会在3个工作日内处理\n周六日节假日除外");
            mTvBackHome.setText("返回首页");
        } else if (mStatus == 1) {
            mIvStatus.setBackgroundResource(R.mipmap.mine_ic_real_name_success);
            mTvStatus.setText("您已通过实名认证");
            mTvTip.setText(null);
            mTvBackHome.setText("我知道了");
        } else {
            mIvStatus.setBackgroundResource(R.mipmap.mine_ic_real_name_fail);
            mTvStatus.setText("实名认证失败");
            mTvTip.setText(!TextUtils.isEmpty(mErrorMsg) ? mErrorMsg : "证件信息与真人认证不匹配");
            mTvTip.setTextColor(ContextCompat.getColor(this, R.color.red));
            mIvFail.setVisibility(View.VISIBLE);
            mTvBackHome.setText("重新认证");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back_home) {  //返回首页
            if (mStatus == 0) {
                ARouterUtils.toMainActivity();
            } else if (mStatus != 1) {
                if (isInPartySelf) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_SELF_CHECK);
                } else {
                    startActivity(RealNameActivity.class);
                }
            }
            finish();
        }
    }


}
