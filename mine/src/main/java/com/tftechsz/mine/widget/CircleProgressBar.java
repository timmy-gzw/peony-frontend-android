package com.tftechsz.mine.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tftechsz.mine.R;


public class CircleProgressBar extends View {
    private int height;
    private int width;
    private Paint mPaint;
    private int strokeWidth = 5;//线条宽度
    private RectF rectF;
    private int normalColor = Color.parseColor("#4DF8D423");//普通的颜色
    private int progressColor = Color.parseColor("#FE4D6B");//已经走了的进度条颜色
    private int textColor = normalColor;//文字颜色
    private float textSize = 20;//文字大小
    private float progress = 0;//进度条
    private String centerText = "100%";//中心填充文字
    private Paint fontPaint = null;
    private Paint.Style progress_style = Paint.Style.STROKE;//填充式还是环形式

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        textSize = array.getDimension(R.styleable.CircleProgressBar_text_size, textSize);
        textColor = array.getColor(R.styleable.CircleProgressBar_text_color, textColor);
        centerText = array.getString(R.styleable.CircleProgressBar_text) == null ? centerText : array.getString(R.styleable.CircleProgressBar_text);
        strokeWidth = array.getInteger(R.styleable.CircleProgressBar_strokewidth, strokeWidth);
        normalColor = array.getColor(R.styleable.CircleProgressBar_normal_color, normalColor);
        progressColor = array.getColor(R.styleable.CircleProgressBar_progress_color, progressColor);
        progress = array.getFloat(R.styleable.CircleProgressBar_progress, progress);
        progress_style = array.getInt(R.styleable.CircleProgressBar_progress_style, 0) == 0 ? Paint.Style.STROKE : Paint.Style.FILL;

        array.recycle();
        initPaint();
    }

    // 2.初始化画笔
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(normalColor);       //设置画笔颜色
        mPaint.setAntiAlias(true);
        mPaint.setStyle(progress_style);  //设置画笔模式为描边
        mPaint.setStrokeWidth(strokeWidth);         //设置画笔宽度为10px

        fontPaint = new Paint();
        fontPaint.setTextSize(textSize);
        fontPaint.setAntiAlias(true);
        fontPaint.setColor(textColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        if (height > width)//高大于宽的情况
        {
            rectF = new RectF(strokeWidth, (height / 2 - width / 2) + strokeWidth, width - strokeWidth, (height / 2 + width / 2) - strokeWidth);
        } else if (width > height)//宽大于高的情况
        {
            rectF = new RectF((width / 2 - height / 2) + strokeWidth, strokeWidth, (width / 2 + height / 2) - strokeWidth, height - strokeWidth);
        } else//宽等于高的情况
        {
            rectF = new RectF(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    Paint.FontMetrics fontMetrics = null;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(normalColor);
        if (progress < 360) {
            canvas.drawArc(rectF, 270 + progress, 360 - progress, progress_style == Paint.Style.FILL, mPaint);
        }

        mPaint.setColor(progressColor);
        canvas.drawArc(rectF, 270, progress, progress_style == Paint.Style.FILL, mPaint);

        fontMetrics = fontPaint.getFontMetrics();
    }


    /**
     * 更新界面
     *
     * @param progress
     */
    public void update(float progress) {
        this.progress = progress;
        postInvalidate();
    }

}
