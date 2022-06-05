package com.tftechsz.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.R;

public class Round2ImageView extends AppCompatImageView {

    private int radius;

    public Round2ImageView(Context context) {
        this(context, null);
    }

    public Round2ImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Round2ImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        radius = ta.getDimensionPixelSize(R.styleable.RoundImageView_radius, DensityUtils.dp2px(context, 6));
        ta.recycle();
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            Path path = new Path();
            path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, Path.Direction.CW);
            canvas.clipPath(path);//设置可显示的区域，canvas四个角会被剪裁掉
//        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
