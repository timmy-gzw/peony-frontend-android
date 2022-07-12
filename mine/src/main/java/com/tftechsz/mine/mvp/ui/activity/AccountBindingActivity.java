package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.NetworkUtils;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActAccountBindBinding;
import com.tftechsz.mine.entity.req.BindData;
import com.tftechsz.mine.entity.req.GetBindData;
import com.tftechsz.mine.mvp.IView.IAccountBindingView;
import com.tftechsz.mine.mvp.presenter.AccountBindingPresenter;

import androidx.databinding.DataBindingUtil;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 账号绑定
 */
@Route(path = ARouterApi.ACTIVITY_ACCOUNT_BINDING)
public class AccountBindingActivity extends BaseMvpActivity<IAccountBindingView, AccountBindingPresenter> implements IAccountBindingView {

    private ActAccountBindBinding mBind;
    private GetBindData mBindData;

    @Override
    public AccountBindingPresenter initPresenter() {
        return new AccountBindingPresenter();
    }


    @Override
    protected void onResume() {
        super.onResume();
        p.getBindData();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_account_bind);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().setTitle("账号绑定").showBack(true).build();
        initRxBus();
        p.getBindData();
        mBind.itemPhone.setOnClickListener(v -> {
            if (!NetworkUtils.isConnected()) {
                Utils.toast("很抱歉，好像网络出问题了");
                return;
            }
            Intent intent = new Intent(mContext, BindPhoneHintActivity.class);
            intent.putExtra(Interfaces.EXTRA_DATA, mBindData);
            startActivity(intent);
        });
        mBind.itemWechat.setOnClickListener(v -> {
            if (!NetworkUtils.isConnected()) {
                Utils.toast("很抱歉，好像网络出问题了");
                return;
            }
            if(mBindData.wecaht.is_bind == 1){
                //微信解绑
                Intent intent = new Intent(mContext, UnbindWechatActivity.class);
                intent.putExtra(Interfaces.EXTRA_DATA, mBindData);
                startActivity(intent);
            }else{
                p.loginWx(mActivity);
            }
        });

    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_WX_LOGIN_SUCCESS) {
                                BindData data = new BindData("wechat", new BindData.DataDTO(null, event.code));
                                p.bindData(mBind.itemWechat.isEnabled(), data);
                            } else if (event.type == Constants.NOTIFY_BIND_PHONE_SUCCESS) {  //绑定成功
                                p.getBindData();
                            }
                        }
                ));
    }

    @Override
    public void getBindDataSuccess(GetBindData data) {
        mBindData = data;
        mBind.itemPhone.setRightText(data.phone != null && data.phone.is_bind == 1 ? data.phone.value : "去绑定");
        if (data.wecaht != null && data.wecaht.is_bind == 1) {
            mBind.itemWechat.setRightText("已绑定");
            mBind.itemWechat.setRightTextColor(mContext, R.color.color_999999);
        } else {
            mBind.itemWechat.setRightText("去绑定");
            mBind.itemWechat.setRightTextColor(mContext, R.color.color_mid_font);
        }

        if (data.wecaht != null && data.wecaht.is_repair == 1) {
            new ToolBarBuilder().setRightText("微信授权", v -> {
                if (!NetworkUtils.isConnected()) {
                    Utils.toast("很抱歉，好像网络出问题了");
                    return;
                }
                p.loginWx(mActivity);
            }).build();
        } else {
            new ToolBarBuilder().showRightTxt(false).build();
        }
        mBind.itemPhone.setRightTextColor(mContext, data.phone != null && data.phone.is_bind == 1 ? R.color.color_999999 : R.color.color_mid_font);
    }

    @Override
    public void bindPhoneSuccess(String data) {
        toastTip("绑定成功!");
        p.getBindData();
    }

    @Override
    public void unBindThirdSuccess(BaseResponse data) {

    }
}
