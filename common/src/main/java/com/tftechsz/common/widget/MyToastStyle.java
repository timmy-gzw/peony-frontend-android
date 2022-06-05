package com.tftechsz.common.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

import com.hjq.toast.style.BlackToastStyle;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.Utils;

/**
 * 包 名 : com.tftechsz.common.widget
 * 描 述 : TODO
 */
public class MyToastStyle extends BlackToastStyle {


    @Override
    protected int getHorizontalPadding(Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
    }


    @Override
    protected int getVerticalPadding(Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
    }

    @Override
    protected Drawable getBackgroundDrawable(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        // 设置颜色
        drawable.setColor(Utils.getColor(R.color.black));
        // 设置圆角
        drawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics()));
        return drawable;
    }

}
