package com.netease.nim.uikit.business;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class AnimationUtil {
    /**
     * 放大移动动画
     */
    public static ObjectAnimator createMove(final ImageView target, int  height, Callback back) {
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", 0, height);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 4.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 4.0f);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(target,translationY,scaleX,scaleY);
        scale.setDuration(2000);
        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                back.animationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return scale;
    }

    /**
     *  放大缩小
     */
    public static ScaleAnimation createScaleXY(Callback back){
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(2);
        scaleAnimation.setDuration(400);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                back.animationEnd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return scaleAnimation;
    }

    /**
     *  缩小移动底部
     */
    public static ObjectAnimator createMoveToBottom(ImageView target, int fromHeight,int  height){
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", fromHeight, height);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",  1.0f, 0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 4.0f, 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 4.0f, 1.0f);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(target,translationY,alpha,scaleX,scaleY);
        scale.setDuration(2000);
        return scale;
    }


    public interface Callback {
        void animationEnd();
    }
}