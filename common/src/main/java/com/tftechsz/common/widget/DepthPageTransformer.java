package com.tftechsz.common.widget;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class DepthPageTransformer implements ViewPager.PageTransformer {

    /**
     * position取值特点：
     * 假设页面从0～1，则：
     * 第一个页面position变化为[0,-1]
     * 第二个页面position变化为[1,0]
     */
    @Override
    public void transformPage(View page, float position) {
        float MIN_ALPHA = 0.5f;
        if (position < -1 || position > 1) {
            page.setAlpha(MIN_ALPHA);
        } else {
            if (position < 0) {//[0,-1]
                page.setAlpha(MIN_ALPHA + (1 + position) * (1 - MIN_ALPHA));
            } else {//[1,0]
                page.setAlpha(MIN_ALPHA + (1 - position) * (1 - MIN_ALPHA));
            }
        }
    }

}
