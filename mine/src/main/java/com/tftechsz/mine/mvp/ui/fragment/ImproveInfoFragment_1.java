package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.OnKeyboardListener;
import com.tftechsz.common.base.BaseFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.ImproveInfoEvent;

public
/**
 *  包 名 : com.tftechsz.mine.mvp.ui.fragment

 *  描 述 : TODO
 */
class ImproveInfoFragment_1 extends BaseFragment implements View.OnClickListener {

    private ImageView mIvGirl;
    private ImageView mIvBoy;
    private ImageView mIvSelBoy;
    private ImageView mIvSelGirl;
    private TextView mTvGirl;
    private TextView mTv_boy;

    @Override
    public void initUI(Bundle savedInstanceState) {
        mIvGirl = getView(R.id.iv_girl);
        mIvBoy = getView(R.id.iv_boy);
        mIvSelBoy = getView(R.id.iv_sel_boy);
        mIvSelGirl = getView(R.id.iv_sel_girl);
        mTvGirl = getView(R.id.tv_girl);
        mTv_boy = getView(R.id.tv_boy);
        selectIcon(1);
        mIvGirl.setOnClickListener(this);
        mIvBoy.setOnClickListener(this);
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .keyboardEnable(false).setOnKeyboardListener(new OnKeyboardListener() {
            @Override
            public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
                RxBus.getDefault().post(new ImproveInfoEvent(isPopup));
            }
        }).init();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_improve_info_1;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_girl) {
            selectIcon(1);
        } else if (id == R.id.iv_boy) {
            selectIcon(2);
        }
    }

    public void selectIcon(int i) {
        RxBus.getDefault().post(new ImproveInfoEvent(i));
        switch (i) {
            case 1: //女孩选中
                mIvGirl.setImageResource(R.mipmap.peony_tx_girl02_img);
                mIvSelGirl.setImageResource(R.mipmap.peony_tx_xz_icon);
                mTvGirl.setTextColor(Utils.getColor(R.color.color_333333));

                mIvBoy.setImageResource(R.mipmap.peony_tx_boy01_img);
                mIvSelBoy.setImageResource(R.mipmap.peony_tx_wxz_icon);
                mTv_boy.setTextColor(Utils.getColor(R.color.color_cc));

                break;

            case 2: //男孩选中
                mIvGirl.setImageResource(R.mipmap.peony_tx_girl01_img);
                mIvSelGirl.setImageResource(R.mipmap.peony_tx_wxz_icon);
                mTvGirl.setTextColor(Utils.getColor(R.color.color_cc));

                mIvBoy.setImageResource(R.mipmap.peony_tx_boy02_img);
                mIvSelBoy.setImageResource(R.mipmap.peony_tx_xz_icon);
                mTv_boy.setTextColor(Utils.getColor(R.color.color_333333));
                break;
        }
    }
}
