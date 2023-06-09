package com.tftechsz.common.utils;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * RecycleView 滚动动画：目标子项顶部对齐父view顶部
 */
public class TopSmoothScroller extends LinearSmoothScroller {

    private float speed = 0;
    public static float DEFAULT_SPEED = 0.6f;

    public TopSmoothScroller(Context context) {
        super(context);
    }

    public TopSmoothScroller(Context context,float speed) {
        super(context);
        this.speed = speed;
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;  // 将子view与父view顶部对齐
    }

    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return super.computeScrollVectorForPosition(targetPosition);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        //返回滑动一个pixel需要多少毫秒
        float s = DEFAULT_SPEED;
        if(this.speed >0){
            s = this.speed;
        }
        return s/displayMetrics.density;
    }


}

