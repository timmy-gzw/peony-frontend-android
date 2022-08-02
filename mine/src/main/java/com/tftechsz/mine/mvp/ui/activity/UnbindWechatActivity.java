package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActUnbindWechatHintBinding;
import com.tftechsz.mine.entity.req.GetBindData;
import com.tftechsz.mine.mvp.IView.IAccountBindingView;
import com.tftechsz.mine.mvp.presenter.AccountBindingPresenter;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 微信绑定（解绑）
 */
public class UnbindWechatActivity extends BaseMvpActivity<IAccountBindingView, AccountBindingPresenter> implements IAccountBindingView {

    private ActUnbindWechatHintBinding mBind;
    private GetBindData mBindData;

    @Override
    public AccountBindingPresenter initPresenter() {
        return new AccountBindingPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_unbind_wechat_hint);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().setTitle("绑定微信").showBack(true).build();
        mBind.setIsBind(true);
        mBind.btn.setOnClickListener(v -> {
            p.unBindThird("wechat");
        });
    }

    @Override
    protected void initData() {
        mBindData = (GetBindData) getIntent().getSerializableExtra(Interfaces.EXTRA_DATA);
       if(null != mBindData.wecaht.value){
           String str= "";
           for (int i = 0; i < mBindData.wecaht.value.length()-1; i++) {
                str += "*";
           }
           if(str.length()<2){
               str = "**";
           }
           mBind.wx.setText(mBindData.wecaht.value.substring(0,1)+str);
           mBind.wx.setVisibility(View.VISIBLE);
       }
    }

    @Override
    public void getBindDataSuccess(GetBindData data) {
        mBindData = data;
    }

    @Override
    public void bindPhoneSuccess(String data) {

    }

    @Override
    public void unBindThirdSuccess(BaseResponse data) {
        toastTip("解绑成功!");
        mBind.btn.setEnabled(false);
        Utils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },500);
    }
}
