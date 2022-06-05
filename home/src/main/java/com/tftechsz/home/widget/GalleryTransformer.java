package com.tftechsz.home.widget;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class GalleryTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
        float scale = 0.5f;
        float scaleValue = 1 - Math.abs(position) * scale;
        view.setScaleX(scaleValue);
        view.setScaleY(scaleValue);
        view.setAlpha(scaleValue);
        view.setPivotX((float) (view.getWidth() * (1 - position - (position > 0 ? 1.5 : -1.5)) * scale));
        view.setPivotY((float) (view.getHeight() / 2));
        view.setElevation(position > -0.25 && position < 0.25 ? 1 : 0);
    }
}
