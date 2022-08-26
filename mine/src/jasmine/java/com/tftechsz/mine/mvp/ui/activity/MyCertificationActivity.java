package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IMyCertificationView;
import com.tftechsz.mine.mvp.presenter.MyCertificationPresenter;

/**
 * 我的认证
 */
@Route(path = ARouterApi.ACTIVITY_MY_CERTIFICATION)
public class MyCertificationActivity extends BaseMvpActivity<IMyCertificationView, MyCertificationPresenter> implements View.OnClickListener, IMyCertificationView {

    private UserInfo mUserInfo;
    //是否实名
    private TextView mTvRealStatus;
    //是否真人
    private TextView mTvSelfStatus;

    @Override
    public MyCertificationPresenter initPresenter() {
        return new MyCertificationPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_my_cartification;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).transparentStatusBar().navigationBarDarkIcon(false).navigationBarColor(R.color.black).statusBarDarkFont(false, 0.2f).init();
        new ToolBarBuilder().showBack(true)
                .setTitle("我的认证")
                .setTitleColor(R.color.white)
                .setBackgroundColor(0)
                .setBackTint(R.color.white)
                .build();

        //是否实名
        mTvSelfStatus = findViewById(R.id.tv_self_status);
        //是否真人
        mTvRealStatus = findViewById(R.id.tv_real_status);

        initListener();
    }

    private void initListener() {
        mTvSelfStatus.setOnClickListener(this);
        mTvRealStatus.setOnClickListener(this);
    }

    private void getUserInfo() {
        if (p != null) {
            p.getUserInfoDetail();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    @Override
    public void onClick(View view) {
        if (isFastClick()) return;
        int id = view.getId();
        if (null == mUserInfo)
            return;
        if (id == R.id.tv_real_status || id == R.id.ll_auth) {  //是否真人
            p.getRealInfo();
        } else if (id == R.id.tv_self_status) {  //是否实名
            if (mUserInfo.isPartyGirl() || mUserInfo.getIs_real() == 1) {
                p.getSelfInfo();
            } else {
                CustomPopWindow customPopWindow = new CustomPopWindow(this);
                customPopWindow.setContent("需要先通过真人认证后才能实名认证哦～");
                customPopWindow.setLeftButton("暂不认证");
                customPopWindow.setRightButton("去真人认证");
                customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSure() {
                        p.getRealInfo();
                    }
                });
                customPopWindow.showPopupWindow();
            }
        }
    }

    @Override
    public void getUserInfoSuccess(UserInfo userInfo) {
        mUserInfo = userInfo;

        setUserInfo();
    }

    /**
     * 设置个人用户信息
     */
    private void setUserInfo() {
        if (null == mUserInfo) return;
        if (mUserInfo.getIs_self() == 1) {   //已经是否实名
            mTvSelfStatus.setText(R.string.certificated);
            mTvSelfStatus.setEnabled(false);
        } else {
            mTvSelfStatus.setText(R.string.to_cert);
            mTvSelfStatus.setEnabled(true);
        }
        if (mUserInfo.getIs_real() == 1) {   //已经真人认证
            mTvRealStatus.setText(R.string.certificated);
            mTvRealStatus.setEnabled(false);
        } else {
            mTvRealStatus.setText(R.string.to_cert);
            mTvRealStatus.setEnabled(true);
        }
    }


    /**
     * 真人认证状态
     *
     * @param data // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
     */
    @Override
    public void getRealInfoSuccess(RealStatusInfoDto data) {
        if (data != null) {
            CommonUtil.performReal(data);
        }
    }

    /**
     * 实名认证状态
     *
     * @param data // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
     */
    @Override
    public void getSelfInfoSuccess(RealStatusInfoDto data) {
        if (data != null) {
            if (mUserInfo.isPartyGirl()) {
                CommonUtil.performSelf(data, ARouterApi.ACTIVITY_SELF_CHECK, Interfaces.SHOW_IS_PARTY_SELF);
            } else {
                CommonUtil.performSelf(data);
            }
        }
    }
}
