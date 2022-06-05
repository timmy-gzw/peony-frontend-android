package com.tftechsz.party.widget.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.tftechsz.party.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 开始PK弹窗
 */
public class StartPkPopWindow extends BasePopupWindow {

    private final LottieAnimationView lottieAnimationView;

    public StartPkPopWindow(Activity context) {
        super(context);
        lottieAnimationView = findViewById(R.id.animation_view);
    }


    public void initData() {
        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    lottieAnimationView.removeAllAnimatorListeners();
                    lottieAnimationView.clearAnimation();
                    listener.onEndAnimation();
                    dismiss();
                }
            }
        });
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_start_pk);
    }


    public interface OnSelectListener {
        void onEndAnimation();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
