package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.im.R;

public class IntmacyUpgradePopWindow extends BaseCenterPop {

    public IntmacyUpgradePopWindow(Context context,View.OnClickListener clickListener) {
        super(context);
        if( null != clickListener) {
            findViewById(R.id.tv_go_chat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    clickListener.onClick(v);
                }
            });
        }
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_intmacy_upgrade);
    }
}
