package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActBindPhoneHintBinding;
import com.tftechsz.mine.entity.req.GetBindData;
import com.tftechsz.mine.mvp.IView.IAccountBindingView;
import com.tftechsz.mine.mvp.presenter.AccountBindingPresenter;

import androidx.databinding.DataBindingUtil;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : TODO
 */
public class BindPhoneHintActivity extends BaseMvpActivity<IAccountBindingView, AccountBindingPresenter> implements IAccountBindingView {

    private ActBindPhoneHintBinding mBind;
    private int bindPhoneType = 2;  // 0:注册绑定  1:换绑  2:未绑定
    private GetBindData mBindData;

    @Override
    public AccountBindingPresenter initPresenter() {
        return new AccountBindingPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_bind_phone_hint);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().setTitle("绑定手机号").showBack(true).build();
        mBind.setIsBind(false);
        mBind.setIsBtnEnable(false);
        mBind.btn.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, BindPhoneActivity.class);
            intent.putExtra(Interfaces.EXTRA_TYPE, bindPhoneType);
            startActivity(intent);
        });
    }

    @Override
    protected void initData() {
        mBindData = (GetBindData) getIntent().getSerializableExtra(Interfaces.EXTRA_DATA);
        setData();
        initBus();
    }

    private void setData() {
        if (mBindData != null) {
            mBind.msg.setText(mBindData.message);
            if (mBindData.phone != null) {
                mBind.phone.setText(mBindData.phone.value);
                mBind.setIsBtnEnable(mBindData.phone.wait_day <= 0);
                mBind.setIsBind(mBindData.phone.is_bind == 1);
                if (mBindData.phone.wait_day == 0) {
                    mBind.btn.setText(mBindData.phone.is_bind == 1 ? "替换" : "去绑定");
                } else {
                    mBind.btn.setText(String.format("%s天后可替换", mBindData.phone.wait_day));
                }
                bindPhoneType = mBindData.phone.is_bind == 1 ? 1 : 2;
            }
        }
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_BIND_PHONE_SUCCESS) {  //绑定成功
                                p.getBindData();
                            }
                        }
                ));
    }


    @Override
    public void getBindDataSuccess(GetBindData data) {
        mBindData = data;
        setData();
    }

    @Override
    public void bindPhoneSuccess(String data) {

    }
}
