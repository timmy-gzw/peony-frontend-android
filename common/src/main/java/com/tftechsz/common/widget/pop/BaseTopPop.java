package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;

import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

public abstract class BaseTopPop extends BasePopupWindow {
    public Context mContext;

    public BaseTopPop(Context context) {
        super(context);
        mContext = context;
        setPopupGravity(Gravity.TOP);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById();
    }


    protected abstract View createPopupById();

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.FROM_TOP)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withTranslation(TranslationConfig.TO_TOP)
                .toDismiss();
    }
}
