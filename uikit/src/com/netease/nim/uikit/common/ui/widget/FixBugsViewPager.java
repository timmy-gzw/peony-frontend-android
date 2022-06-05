package com.netease.nim.uikit.common.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 修复ViewPagerbug
 * <p>
 * https://stackoverflow.com/questions/16459196/java-lang-illegalargumentexception-pointerindex-out-of-range-exception-dispat
 */
public class FixBugsViewPager extends ViewPager {

    public FixBugsViewPager(Context context) {
        super(context);
    }

    public FixBugsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
