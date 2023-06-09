package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.mine.R;

/**
 * 账号管理
 */
@Route(path = ARouterApi.ACTIVITY_ACCOUNT_MANAGER)
public class AccountManagerActivity extends BaseMvpActivity implements View.OnClickListener {

    @Override
    protected int getLayout() {
        return R.layout.activity_account_manager;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle("账号管理").build();
        findViewById(R.id.item_account_binding).setOnClickListener(this);
        findViewById(R.id.item_logout_setting).setOnClickListener(this);
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.item_account_binding) {
            ARouterUtils.toAccountBindingActivity();
        } else if (id == R.id.item_logout_setting) {
            ARouterUtils.toCancellationActivity();
        }
    }
}
