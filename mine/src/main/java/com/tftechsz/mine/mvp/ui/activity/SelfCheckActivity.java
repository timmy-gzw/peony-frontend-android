package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AddSpaceTextWatcher;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActSelfCheckBinding;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 实名认证界面
 */
@Route(path = ARouterApi.ACTIVITY_SELF_CHECK)
public class SelfCheckActivity extends BaseMvpActivity implements View.OnClickListener {
    @Autowired
    UserProviderService service;
    private ActSelfCheckBinding mBind;

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_self_check);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle("实名认证").build();
        mBind.ivDelCard.setOnClickListener(this);
        mBind.ivDelName.setOnClickListener(this);
        mBind.btn.setOnClickListener(this);
        Utils.setEditCardTextChangedListener(mBind.edtCard, mBind.ivDelCard, mBind.errorHint);
        Utils.setEditCardTextChangedListener(mBind.edtName, mBind.ivDelName, mBind.errorHint);
        new AddSpaceTextWatcher(mBind.edtCard, AddSpaceTextWatcher.SpaceType.IDCardNumberType);

        mBind.edtCard.setOnFocusChangeListener((v, hasFocus) -> mBind.edtCard.setHint(hasFocus ? null : "请输入"));
        mBind.edtName.setOnFocusChangeListener((v, hasFocus) -> mBind.edtName.setHint(hasFocus ? null : "请输入"));


    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn) {
            String idCard = Utils.getText(mBind.edtCard).replace(" ", "");
            String idName = Utils.getText(mBind.edtName);
            if (TextUtils.isEmpty(idCard)) {
                Utils.setFocus(mBind.edtCard);
                mBind.errorHint.setText("请输入您的身份证号码");
                mBind.errorHint.setVisibility(View.VISIBLE);
                return;
            }
            if (TextUtils.isEmpty(idName)) {
                Utils.setFocus(mBind.edtName);
                mBind.errorHint.setText("请输入您的姓名");
                mBind.errorHint.setVisibility(View.VISIBLE);
                return;
            }
            if (!Utils.regexIdCard(service.getConfigInfo(), idCard)) {
                Utils.setFocus(mBind.edtCard);
                mBind.errorHint.setText("身份证格式错误");
                mBind.errorHint.setVisibility(View.VISIBLE);
                return;
            }
            if (!Utils.regexChineseName(service.getConfigInfo(), idName)) {
                Utils.setFocus(mBind.edtName);
                mBind.errorHint.setText("姓名格式错误");
                mBind.errorHint.setVisibility(View.VISIBLE);
                return;
            }

            KeyboardUtils.hideSoftInput(this);
            mCompositeDisposable.add(RetrofitManager.getInstance()
                    .createUserApi(PublicService.class)
                    .partySelf(idCard, idName)
                    .compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<RealCheckDto>>() {
                        @Override
                        public void onSuccess(BaseResponse<RealCheckDto> response) {
                            if (response.getData() != null) {
                                if (response.getData().pass) {//身份证验证成功, 进行活体
                                    final String[] permissions = {Manifest.permission.CAMERA};
                                    PermissionUtil.beforeCheckPermission(SelfCheckActivity.this, permissions, agreeToRequest -> {
                                        if (agreeToRequest) {
                                            mCompositeDisposable.add(new RxPermissions((FragmentActivity) mActivity).request(permissions)
                                                    .subscribe(aBoolean -> {
                                                        if (aBoolean) {
                                                            ARouterUtils.toFaceCheck(true);
                                                            finish();
                                                        } else {
                                                            PermissionUtil.showPermissionPop(mActivity);
                                                        }
                                                    }));
                                        } else {
                                            PermissionUtil.showPermissionPop(mActivity);
                                        }
                                    });
                                    return;
                                }
                                toastTip(response.getData().msg);
                            }
                        }
                    }));


        } else if (id == R.id.iv_del_card) {
            mBind.edtCard.setText(null);
        } else if (id == R.id.iv_del_name) {
            mBind.edtName.setText(null);
        }
    }
}
