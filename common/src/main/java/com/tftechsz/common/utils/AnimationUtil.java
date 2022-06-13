package com.tftechsz.common.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.tftechsz.common.R;

public class AnimationUtil {
    /**
     * 放大移动动画
     */
    public static ObjectAnimator createMove(final ImageView target, int height, Callback back) {
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", 0, height);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 4.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 4.0f);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(target, translationY, scaleX, scaleY);
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
     * 放大缩小
     *
     * @return
     */
    public static ScaleAnimation createScaleXY(Callback back) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
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
     * 缩小移动底部
     *
     * @param target
     * @return
     */
    public static ObjectAnimator createMoveToBottom(ImageView target, int fromHeight, int height) {
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", fromHeight, height);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 4.0f, 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 4.0f, 1.0f);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(target, translationY, alpha, scaleX, scaleY);
        scale.setDuration(2000);
        return scale;
    }


    public static void createAnimation(ImageView target) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setDuration(400);
        target.startAnimation(scaleAnimation);
    }

    public static void createAvatarAnimation(View target) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setDuration(500);
        target.startAnimation(scaleAnimation);
    }


    public static void createRotateAnimation(ImageView imageView) {
        RotateAnimation animation = new RotateAnimation(0f, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        animation.setRepeatCount(-1);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        imageView.startAnimation(animation);
    }

    public static void createRotateRevertAnimation(ImageView imageView) {
        RotateAnimation animation = new RotateAnimation(0f, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1500);
        animation.setRepeatCount(-1);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        imageView.startAnimation(animation);
    }

    public static void createScaleAnimation(View target) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 3f, 1f, 3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(Animation.RESTART);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setDuration(1500);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f,0f);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setDuration(1500);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        target.startAnimation(animationSet);
    }

    public static void createScaleAlphaAnimation(View target) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setDuration(700);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1f);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setDuration(700);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        target.startAnimation(animationSet);
    }


    public static void invisibleAnimator(final View view, boolean showGone) {
        if (view != null) {
            int viewHeight = view.getHeight();
            if (viewHeight == 0) {
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                view.measure(width, height);
                viewHeight = view.getMeasuredHeight();
            }
            ValueAnimator animator = ValueAnimator.ofInt(viewHeight, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = (int) animation.getAnimatedValue();
                    view.setLayoutParams(params);
                    if (showGone && params.height == 0) {
                        view.setVisibility(View.GONE);
                    }
                }
            });
            animator.start();
        }
    }

    public static void invisibleAnimator(final View view) {
        if (view != null) {
            int viewHeight = view.getHeight();
            if (viewHeight == 0) {
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                view.measure(width, height);
                viewHeight = view.getMeasuredHeight();
            }
            ValueAnimator animator = ValueAnimator.ofInt(viewHeight, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = (int) animation.getAnimatedValue();
                    view.setLayoutParams(params);
                }
            });
            animator.start();
        }
    }

    public static void visibleAnimator(final View view) {
        if (view != null) {
            int viewHeight = view.getHeight();
            if (viewHeight == 0) {
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                view.measure(width, height);
                viewHeight = view.getMeasuredHeight();
            }
            ValueAnimator animator = ValueAnimator.ofInt(0, viewHeight);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = (int) animation.getAnimatedValue();
                    view.setLayoutParams(params);
                }
            });
            animator.start();
        }
    }

    /**
     * 设置播放动画
     */
    public static void setAnimation(ImageView imageView, ImageView imageView1) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.15f, 1f, 1.15f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.5f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        as.setDuration(800);
        as.addAnimation(scaleAnimation);
        as.addAnimation(alphaAnimation);
        imageView.startAnimation(as);
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        AnimationSet as1 = new AnimationSet(true);
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.15f, 1.3f, 1.15f, 1.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //渐变动画
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.5f, 0.1f);
        scaleAnimation1.setDuration(800);
        scaleAnimation1.setRepeatCount(Animation.INFINITE);
        alphaAnimation1.setRepeatCount(Animation.INFINITE);
        as1.setDuration(800);
        as1.addAnimation(scaleAnimation1);
        as1.addAnimation(alphaAnimation1);
        imageView1.startAnimation(as1);
    }


    public interface Callback {
        void animationEnd();
    }

    /**
     * @param context
     * @param view    目标view
     */
    public static Animation startScaleInAnim(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_in_new);
        if (view != null)
            view.startAnimation(animation);

        return animation;
    }

    /**
     * @param context
     * @param view    目标view
     */
    public static Animation startScaleOutAnim(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_outs_new);
        if (view != null)
            view.startAnimation(animation);

        return animation;
    }


    /**
     * @param target
     * @return 送礼数字变化
     */
    public static ObjectAnimator scaleGiftNum(final TextView target) {
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                1.2f, 0.8f, 1f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                1.2f, 0.8f, 1f);
        PropertyValuesHolder anim6 = PropertyValuesHolder.ofFloat("alpha",
                1.0f, 0f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, anim4, anim5, anim6).setDuration(400);
        return animator;

    }
};
