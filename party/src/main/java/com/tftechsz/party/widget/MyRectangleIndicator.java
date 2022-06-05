package com.tftechsz.party.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.blankj.utilcode.util.ConvertUtils;
import com.youth.banner.indicator.BaseIndicator;

/**
 * 包 名 : com.tftechsz.party.widget
 * 描 述 : TODO
 */
public class MyRectangleIndicator extends BaseIndicator {
    RectF rectF;
    private final int normalWidth = ConvertUtils.dp2px(10);
    private final int getSelectedWidth = normalWidth;
    private final int getHeight = ConvertUtils.dp2px(2);
    private final int getRadius = ConvertUtils.dp2px(0);
    private final int getIndicatorSpace = ConvertUtils.dp2px(3);

    public MyRectangleIndicator(Context context) {
        this(context, null);
    }

    public MyRectangleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRectangleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }
        //间距*（总数-1）+默认宽度*（总数-1）+选中宽度
        int space = getIndicatorSpace * (count - 1);
        int normal = normalWidth * (count - 1);
        setMeasuredDimension(space + normal + getSelectedWidth, getHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }
        float left = 0;
        for (int i = 0; i < count; i++) {
            mPaint.setColor(config.getCurrentPosition() == i ? config.getSelectedColor() : config.getNormalColor());
            int indicatorWidth = config.getCurrentPosition() == i ? getSelectedWidth : normalWidth;
            rectF.set(left, 0, left + indicatorWidth, getHeight);
            left += indicatorWidth + getIndicatorSpace;
            canvas.drawRoundRect(rectF, getRadius, getRadius, mPaint);
        }
    }
}
