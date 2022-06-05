package com.tftechsz.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.R;


@SuppressLint("AppCompatCustomView")
public class RoundImageView extends ImageView {
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
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        radius = ta.getDimensionPixelSize(R.styleable.RoundImageView_radius, DensityUtils.dp2px(context, 10));
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
            path.moveTo(radius+pading, 0+pading);
            path.lineTo(width - radius-pading, 0+pading);
            path.quadTo(width-pading, 0+pading, width-pading, radius+pading);
            path.lineTo(width-pading, height - radius-pading);
            path.quadTo(width-pading, height-pading, width - radius-pading, height-pading);
            path.lineTo(radius+pading, height-pading);
            path.quadTo(0+pading, height-pading, 0+pading, height - radius-pading);
            path.lineTo(0+pading, radius+pading);
            path.quadTo(0+pading, 0+pading, radius+pading, 0+pading);
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

    public void setCornerRadius(int cornerRadius) {
        radius = cornerRadius;
    }
}
