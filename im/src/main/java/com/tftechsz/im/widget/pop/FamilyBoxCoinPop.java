package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.im.R;
import com.tftechsz.im.databinding.PopFamilyBoxCoinBinding;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import androidx.databinding.DataBindingUtil;
import razerdp.basepopup.BasePopupWindow;

/**
 * 包 名 : com.tftechsz.im.widget.pop
 * 描 述 : 家族宝箱获得金币
 */
public class FamilyBoxCoinPop extends BaseCenterPop {

    private PopFamilyBoxCoinBinding mBind;

    public FamilyBoxCoinPop(Context context) {
        super(context);
        mBind.btn.setOnClickListener(v -> dismiss());
    }

    public BasePopupWindow setCoin(int coin) {
        mBind.setShowCoin(coin > 0);
        mBind.coin.setText(String.valueOf(coin));
        return this;
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_family_box_coin);
        mBind = DataBindingUtil.bind(view);
        return view;
    }
}
