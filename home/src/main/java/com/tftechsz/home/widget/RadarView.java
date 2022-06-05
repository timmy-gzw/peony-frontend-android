package com.tftechsz.home.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;


/**
 * 雷达
 */
public class RadarView extends View {
    private Paint mPaintLine;//画圆线需要用到的paint
    private Paint mPaintMaxLine;//画圆线需要用到的paint
    private Paint mPaintCircle;//画圆需要用到的paint
    private Paint mPaintScan;//画扫描需要用到的paint
    private int mWidth, mHeight;//整个图形的长度和宽度
    private Matrix matrix = new Matrix();//旋转需要的矩阵
    private int scanAngle;//扫描旋转的角度
    private Shader scanShader;//扫描渲染shader
    private Bitmap centerBitmap;//最中间icon

    //每个圆圈所占的比例
    private static float[] circleProportion = {1 / 24f, 2 / 24f, 3 / 24f, 4 / 24f, 5 / 24f, 6 / 13f};
    private int scanSpeed = 5;
    private Context context;

    private int currentScanningCount;//当前扫描的次数
    private int currentScanningItem;//当前扫描显示的item
    private int maxScanItemCount;//最大扫描次数
    private boolean startScan = false;//只有设置了数据后才会开始扫描
    private IScanningListener iScanningListener;//扫描时监听回调接口

    public void setScanningListener(IScanningListener iScanningListener) {
        this.iScanningListener = iScanningListener;
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            scanAngle = (scanAngle + scanSpeed) % 360;
            matrix.postRotate(scanSpeed, mWidth / 2, mHeight / 2);
            invalidate();
            postDelayed(run, 100);
            //开始扫描显示标志为true 且 只扫描一圈
            if (startScan && currentScanningCount <= (360 / scanSpeed)) {
                if (iScanningListener != null && currentScanningCount % scanSpeed == 0
                        && currentScanningItem < maxScanItemCount) {
                    iScanningListener.onScanning(currentScanningItem, scanAngle);
                    currentScanningItem++;
                } else if (iScanningListener != null && currentScanningItem == maxScanItemCount) {
                    iScanningListener.onScanSuccess();
                }
                currentScanningCount++;
            }
        }
    };

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        this.context = context;
        post(run);
    }


    private void init() {
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.parseColor("#66FFFFFF"));
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(2);
        mPaintLine.setStyle(Paint.Style.STROKE);

        mPaintMaxLine = new Paint();
        mPaintMaxLine.setColor(Color.parseColor("#33FFFFFF"));
        mPaintMaxLine.setAntiAlias(true);
        mPaintMaxLine.setStrokeWidth(2);
        mPaintMaxLine.setStyle(Paint.Style.STROKE);


        mPaintCircle = new Paint();
        mPaintCircle.setColor(Color.WHITE);
        mPaintCircle.setAntiAlias(true);

        mPaintScan = new Paint();
        mPaintScan.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public String mUserAvatar;

    public void setImage(String userAvatar) {
        this.mUserAvatar = userAvatar;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(widthMeasureSpec));
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth, mHeight);
//        GlideUtils.downloadImage(context, mUserAvatar, new GlideUtils.DownloadBitmapSuccessListener() {
//            @Override
//            public void success(Bitmap bp) {
//                centerBitmap = BitMapUtils.circleBitmap(bp);
//            }
//        });
        //设置扫描渲染的shader
        scanShader = new SweepGradient(mWidth / 2, mHeight / 2,
                new int[]{Color.TRANSPARENT, Color.parseColor("#33FFFFFF")}, null);
    }

    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 300;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawScan(canvas);
//        drawCenterIcon(canvas);
    }

    /**
     * 绘制圆线圈
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
//        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * circleProportion[1], mPaintLine);     // 绘制小圆
//        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * circleProportion[3], mPaintMaxLine); // 绘制中大圆
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * circleProportion[5], mPaintLine);  // 绘制大大圆
    }

    /**
     * 绘制扫描
     *
     * @param canvas
     */
    private void drawScan(Canvas canvas) {
        canvas.save();
        mPaintScan.setShader(scanShader);
        canvas.concat(matrix);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * circleProportion[5], mPaintScan);
        canvas.restore();
    }

    /**
     * 绘制最中间的图标
     *
     * @param canvas
     */
    private void drawCenterIcon(Canvas canvas) {
        if (centerBitmap != null)
            canvas.drawBitmap(centerBitmap, null,
                    new Rect((int) (mWidth / 2 - mWidth * circleProportion[0]), (int) (mHeight / 2 - mWidth * circleProportion[0]),
                            (int) (mWidth / 2 + mWidth * circleProportion[0]), (int) (mHeight / 2 + mWidth * circleProportion[0])), mPaintCircle);
    }


    public interface IScanningListener {
        //正在扫描（此时还没有扫描完毕）时回调
        void onScanning(int position, float scanAngle);

        //扫描成功时回调
        void onScanSuccess();
    }

    public void setMaxScanItemCount(int maxScanItemCount) {
        this.maxScanItemCount = maxScanItemCount;
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        this.startScan = true;
    }

    public void stopScan() {
        this.startScan = false;
    }
}
