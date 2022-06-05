package com.netease.nim.uikit.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CenteredImageSpan extends ImageSpan {

    private int width;
    private int height;

    public CenteredImageSpan(Context context, final int drawableRes) {
        super(context, drawableRes);
    }


    public CenteredImageSpan(Context context, final int drawableRes, int width, int height) {
        super(context, drawableRes);
        this.width = width;
        this.height = height;
    }


    public CenteredImageSpan(Context context, final Bitmap bp) {
        super(context, bp);
    }

    public CenteredImageSpan(Drawable d) {
        super(d);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        return super.getSize(paint, text, start, end, fm);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        // image to draw
        Drawable b = getDrawable();
        if (height != 0 && width !=0){
            b.setBounds(0, 0, width, height);
        }
        // font metrics of text to be replaced
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2
                - b.getBounds().bottom / 2;
        canvas.save();

        canvas.translate(x, transY);

        b.draw(canvas);
        canvas.restore();
    }
}