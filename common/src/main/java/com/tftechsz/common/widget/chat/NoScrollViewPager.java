package com.tftechsz.common.widget.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * ================================================
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*-------------------------------  页面切换时的滑动翻页效果禁用  ------------------------------------------*/

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }

    /*-------------------------------  滑动禁用  ------------------------------------------*/
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }

}
