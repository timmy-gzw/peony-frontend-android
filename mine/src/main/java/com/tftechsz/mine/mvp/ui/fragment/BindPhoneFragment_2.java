package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;

import com.tftechsz.common.base.BaseFragment;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.FrgBindPhone2Binding;
import com.tftechsz.mine.widget.PhoneCode;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.fragment
 * 描 述 : TODO
 */
public class BindPhoneFragment_2 extends BaseFragment {

    private FrgBindPhone2Binding mBind;
    private CodeCallBack mBack;

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    public int getBindLayout() {
        return R.layout.frg_bind_phone_2;
    }

    public void setCodeCallBack(CodeCallBack mBack) {
        this.mBack = mBack;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mBind = (FrgBindPhone2Binding) getBind();
        mBind.phoneCode.setOnInputListener(new PhoneCode.OnInputListener() {
            @Override
            public void onSuccess(String code) {
                if (mBack != null) {
                    mBack.codeChange(code);
                }
            }

            @Override
            public void onInput() {

            }
        });
    }

    @Override
    protected void initData() {

    }

    public void showSoftInput() {
        mBind.phoneCode.showSoftInput();
    }

    public interface CodeCallBack {
        void codeChange(String code);
    }
}
