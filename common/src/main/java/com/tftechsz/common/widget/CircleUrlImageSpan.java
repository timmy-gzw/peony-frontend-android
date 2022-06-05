package com.tftechsz.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.tftechsz.common.R;
import com.tftechsz.common.utils.GlideUtils;

import java.lang.reflect.Field;

/**
 * 获取网络图片的ImageSpan
 */
public class CircleUrlImageSpan extends ImageSpan {

    private String url;
    private TextView tv;
    private boolean picShowed;
    private int width;
    private int height;

    public CircleUrlImageSpan(Context context, String url, TextView tv) {
        super(context, R.mipmap.ic_default_small_avatar);
        this.url = url;
        this.tv = tv;
        width = 45;
        height = 45;
    }

    public CircleUrlImageSpan(Context context, String url, TextView tv, int width, int height) {
        super(context, R.mipmap.ic_default_small_avatar);
        this.url = url;
        this.tv = tv;
        this.width = width;
        this.height = height;
    }

    @Override
    public Drawable getDrawable() {
        if (!picShowed) {
            GlideUtils.downloadImage(tv.getContext(), url, width, height, new GlideUtils.DownloadBitmapSuccessListener() {
                @Override
                public void success(Bitmap bp) {
                    Resources resources = tv.getContext().getResources();
                    RoundedBitmapDrawable b = RoundedBitmapDrawableFactory.create(resources, bp);
                    b.setBounds(0, 0, b.getIntrinsicWidth(), b.getIntrinsicHeight());
                    b.setCornerRadius(b.getIntrinsicHeight() / 2);
                    Field mDrawable;
                    Field mDrawableRef;
                    try {
                        mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
                        mDrawable.setAccessible(true);
                        mDrawable.set(CircleUrlImageSpan.this, b);
                        mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
                        mDrawableRef.setAccessible(true);
                        mDrawableRef.set(CircleUrlImageSpan.this, null);
                        picShowed = true;
                        tv.setText(tv.getText());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return super.getDrawable();
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        try {
            Drawable d = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - d.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            d.draw(canvas);
            canvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        try {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;
                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;
                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        } catch (Exception e) {
            return 20;
        }
    }

    /**
     * 按宽度缩放图片
     *
     * @param bmp  需要缩放的图片源
     * @param newW 需要缩放成的图片宽度
     * @return 缩放后的图片
     */
    public static Bitmap zoom(@NonNull Bitmap bmp, int newW) {
        // 获得图片的宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // 计算缩放比例
        float scale = ((float) newW) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
