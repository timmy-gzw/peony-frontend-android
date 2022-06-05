package com.tftechsz.common.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.viewpager2.widget.ViewPager2;

/**
 * 包 名 : com.tftechsz.common.widget
 * 描 述 : ViewPager2改变动画切换速度 https://blog.csdn.net/HBK_MySummerCT/article/details/104534851
 */
public class MyPagerHelper {
    /**
     * 保存前一个animatedValue
     */
    private static int previousValue;

    /**
     * 设置当前Item
     * @param pager    viewpager2
     * @param item     下一个跳转的item
     * @param duration scroll时长
     */
    public static void setCurrentItem(final ViewPager2 pager, int item, long duration) {
        previousValue = 0;
        int currentItem = pager.getCurrentItem();
        int pagePxWidth = pager.getWidth();
        int pxToDrag = pagePxWidth * (item - currentItem);
        final ValueAnimator animator = ValueAnimator.ofInt(0, pxToDrag);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                float currentPxToDrag = (float) (currentValue - previousValue);
                pager.fakeDragBy(-currentPxToDrag);
                previousValue = currentValue;
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                pager.beginFakeDrag();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pager.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();
    }

}
