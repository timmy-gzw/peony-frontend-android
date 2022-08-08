package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.mine.R;

public class RechargeSuccessPop extends BaseCenterPop {
    public RechargeSuccessPop(Context context) {
        super(context);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_recharge_success);
    }
}
