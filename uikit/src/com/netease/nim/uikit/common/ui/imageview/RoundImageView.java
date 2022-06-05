package com.netease.nim.uikit.common.ui.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatImageView;

import com.netease.nim.uikit.R;


public class RoundImageView extends AppCompatImageView {
    private float width, height;
    private Path path = new Path();
    private int radius;
    private float pading = getResources().getDimension(R.dimen.dp_0);

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.roundImageView);
        radius = ta.getDimensionPixelSize(R.styleable.roundImageView_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, context.getResources().getDisplayMetrics()));
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width > radius && height > radius) {
            path.reset();
            path.moveTo(radius + pading, 0 + pading);
            path.lineTo(width - radius - pading, 0 + pading);
            path.quadTo(width - pading, 0 + pading, width - pading, radius + pading);
            path.lineTo(width - pading, height - radius - pading);
            path.quadTo(width - pading, height - pading, width - radius - pading, height - pading);
            path.lineTo(radius + pading, height - pading);
            path.quadTo(0 + pading, height - pading, 0 + pading, height - radius - pading);
            path.lineTo(0 + pading, radius + pading);
            path.quadTo(0 + pading, 0 + pading, radius + pading, 0 + pading);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }

    public void setCornerRadius(int cornerRadius) {
        radius = cornerRadius;
    }
}
