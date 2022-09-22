/**
 * @auther gejiahui
 * created at 2022/9/22/18:14
 */

package com.tftechsz.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 *@auther gjh
 */
public class NoScrollVP extends ViewPager {
    public NoScrollVP(Context context) {
        this(context, null);
    }
    public NoScrollVP(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;// 不拦截事件
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 与业务需求挂钩
        return true;// 子view拦截了但不处理，则自己再处理
    }}
