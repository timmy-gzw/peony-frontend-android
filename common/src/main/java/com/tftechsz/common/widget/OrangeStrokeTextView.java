package com.tftechsz.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 加描边颜色
 */

public class OrangeStrokeTextView extends AppCompatTextView {
    private final TextView outlineTextView;

    public OrangeStrokeTextView(Context context) {
        super(context);

        outlineTextView = new TextView(context);
        init();
    }

    public OrangeStrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        outlineTextView = new TextView(context, attrs);
        init();
    }

    public OrangeStrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        outlineTextView = new TextView(context, attrs, defStyle);
        init();
    }

    public void init() {
        TextPaint paint = outlineTextView.getPaint();
        paint.setStrokeWidth(4);  //描边宽度
        paint.setStyle(Paint.Style.STROKE);
        outlineTextView.setTextColor(Color.parseColor("#F89B23"));  //描边颜色
        outlineTextView.setGravity(getGravity());

        outlineTextView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/youshebiaotihei.ttf"));
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/youshebiaotihei.ttf"));
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        outlineTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置轮廓文字
        CharSequence outlineText = outlineTextView.getText();
        if (outlineText == null || !outlineText.equals(this.getText())) {
            outlineTextView.setText(getText());
            postInvalidate();
        }
        outlineTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        outlineTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        outlineTextView.draw(canvas);
        super.onDraw(canvas);
    }
}
