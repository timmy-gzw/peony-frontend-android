package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.tftechsz.common.R;

import razerdp.basepopup.BasePopupWindow;

public abstract class BaseCenterPop extends BasePopupWindow {
    public Context mContext;

    public BaseCenterPop(Context context) {
        super(context);
        mContext = context;
        setPopupGravity(Gravity.CENTER);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById();
    }


    protected abstract View createPopupById();

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_enter_anim);
    }


    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_exit_anim);
    }

}
