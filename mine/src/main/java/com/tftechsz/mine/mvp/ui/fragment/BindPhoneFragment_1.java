package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.tftechsz.common.base.BaseFragment;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.AddSpaceTextWatcher;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.FrgBindPhone1Binding;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.fragment
 * 描 述 : TODO
 */
public class BindPhoneFragment_1 extends BaseFragment {

    private FrgBindPhone1Binding mBind;
    private String mTell;
    private EdtCallBack edtCallBack;

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    public int getBindLayout() {
        return R.layout.frg_bind_phone_1;
    }

    public void setEdtCallBack(EdtCallBack edtCallBack) {
        this.edtCallBack = edtCallBack;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTell = getArguments().getString(Interfaces.EXTRA_PHONE);
        }
        mBind = (FrgBindPhone1Binding) getBind();
        mBind.ivDel.setOnClickListener(v -> mBind.edtPhone.setText(null));
        mBind.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtCallBack != null) {
                    edtCallBack.edtChange(s.toString());
                }
                mBind.ivDel.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });
        new AddSpaceTextWatcher(mBind.edtPhone, AddSpaceTextWatcher.SpaceType.mobilePhoneNumberType);
        mBind.edtPhone.setText(mTell);
    }

    @Override
    protected void initData() {

    }

    public interface EdtCallBack {
        void edtChange(String str);
    }
}
