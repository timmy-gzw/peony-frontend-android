package com.tftechsz.main.widget;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.main.R;

public class YouthModelPop extends BaseCenterPop implements View.OnClickListener {

    public YouthModelPop(Context context) {
        super(context);
        findViewById(R.id.tv_open).setOnClickListener(this);
        findViewById(R.id.tv_know).setOnClickListener(this);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_youth_model);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.tv_know){
            dismiss();
        }else if(id == R.id.tv_open){
            ARouterUtils.toYouthModelActivity();
            dismiss();
        }
    }
}
