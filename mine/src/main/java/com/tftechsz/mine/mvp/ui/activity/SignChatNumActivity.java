package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.SignNumBean;
import com.tftechsz.mine.mvp.IView.ISettingView;
import com.tftechsz.mine.mvp.presenter.SettingPresenter;

/**
 * 聊天卡
 */
@Route(path = ARouterApi.ACTIVITY_SIGN_CHAT_NUM)
public class SignChatNumActivity extends BaseMvpActivity<ISettingView, SettingPresenter> implements View.OnClickListener, ISettingView {

    @Autowired
    UserProviderService service;

    @Override
    public SettingPresenter initPresenter() {
        return new SettingPresenter();
    }

    private TextView mTvNumChatSign, mTvDes;

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("聊天卡")
                .setRightText("消耗记录", v -> ARouterUtils.toIntegralDetailedActivity(3))
                .build();
        mTvNumChatSign = findViewById(R.id.tv_num_chat);
        mTvDes = findViewById(R.id.tv_des);
        p.getSignChatNum();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_sign_chat_num;
    }


    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void exitSuccess(boolean data) {

    }

    @Override
    public void num(SignNumBean bean) {
        mTvNumChatSign.setText(String.valueOf(bean.cost));
        mTvDes.setText(bean.desc);
    }
}
