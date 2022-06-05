package com.tftechsz.mine.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;

/**
 * 包 名 : com.tftechsz.mine.widget
 * 描 述 : 贵族文本颜色渐变
 */
public class NobleGradientColorTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final Rect mTextBound = new Rect();

    public NobleGradientColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int viewWidth = getMeasuredWidth();
        Paint paint = getPaint();
        String mTipText = getText().toString();
        paint.getTextBounds(mTipText, 0, mTipText.length(), mTextBound);
        @SuppressLint("DrawAllocation") LinearGradient linearGradient = new LinearGradient(0, 0, viewWidth, 0,
                new int[]{Color.parseColor("#FFE7BD"), Color.parseColor("#FFD07D")},
                null, Shader.TileMode.REPEAT);
        paint.setShader(linearGradient);
        canvas.drawText(mTipText, (viewWidth >> 1) - (mTextBound.width() >> 1), (getMeasuredHeight() >> 1) + (mTextBound.height() >> 1), paint);
    }
}
