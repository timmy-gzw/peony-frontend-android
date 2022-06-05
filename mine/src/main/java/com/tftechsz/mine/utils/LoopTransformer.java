package com.tftechsz.mine.utils;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * 包 名 : com.tftechsz.mine.utils
 * 描 述 : TODO
 */
public class LoopTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;


    private static final float MIN_ALPHA = 0.5f;
    private static final float MAX_ROTATE = 30;

    @Override
    public void transformPage(View page, float position) {
        /**
         * 过滤那些 <-1 或 >1 的值，使它区于【-1，1】之间
         */
      /*  if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }
        *//**
         * 判断是前一页 1 + position ，右滑 pos -> -1 变 0
         * 判断是后一页 1 - position ，左滑 pos -> 1 变 0
         *//*
        float tempScale = position < 0 ? 1 + position : 1 - position; // [0,1]
        float scaleValue = MIN_SCALE + tempScale * 0.1f; // [0,1]
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);*/

        float centerX = page.getWidth() / 2;
        float centerY = page.getHeight() / 2;
        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
        float rotate = 20 * Math.abs(position);
        if (position < -1) {

        } else if (position < 0) {
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(rotate);
        } else if (position >= 0 && position < 1) {
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(-rotate);
        } else if (position >= 1) {
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(-rotate);
        }

    }
}
