package com.tftechsz.home.widget.carrousellayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.home.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * 旋转木马布局
 * Created by dalong on 2016/11/12.
 */

public class CarrouselLayout extends RelativeLayout {

    //旋转间隔时间  默认设置为2秒
    private int mRotationTime = 3000;

    //旋转木马旋转半径  圆的半径
    private float mCarrouselR = 65;

    //camera和旋转木马距离
    private float mDistance = 2f * mCarrouselR;

    private final int msgWhat = 1;
    //x旋转
    private int mRotationX;

    //Z旋转
    private int mRotationZ;

    //旋转的角度
    private float mAngle = 0;

    //旋转木马子view
    private final List<View> mCarrouselViews = new ArrayList<>();

    //旋转木马子view的数量
    private int viewCount;

    //半径扩散动画
    private ValueAnimator mAnimationR;

    //旋转动画
    private ValueAnimator restAnimator;

    //选中item
    private int selectItem;

    //x轴旋转动画
    private ValueAnimator xAnimation;

    //z轴旋转动画
    private ValueAnimator zAnimation;

    public CarrouselLayout(Context context) {
        this(context, null);
    }

    public CarrouselLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarrouselLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CarrouselLayout);
        mRotationTime = typedArray.getInt(R.styleable.CarrouselLayout_rotationTime, 2000);
        mCarrouselR = typedArray.getDimension(R.styleable.CarrouselLayout_r, 65);
        typedArray.recycle();
//        mGestureDetector = new GestureDetector(context, getGestureDetectorController());
        setR(DensityUtils.dp2px(context, 22));//设置R的大小
        handler.sendEmptyMessage(msgWhat);

    }


    /**
     * 初始化 计算平均角度后各个子view的位置
     */
    public void refreshLayout() {
        if (mCarrouselViews == null) {
            return;
        }
        for (int i = 0; i < mCarrouselViews.size(); i++) {
            double radians = mAngle + 180 - i * 360 / viewCount;
            float x0 = (float) Math.sin(Math.toRadians(radians)) * mCarrouselR;
            float y0 = (float) Math.cos(Math.toRadians(radians)) * mCarrouselR;
            float scale0 = (mDistance - y0) / (mDistance + mCarrouselR);
            scale0 = scale0 < 0.75 ? 0.75f : scale0;//控制头像大小

            float rotationX_y = (float) Math.sin(Math.toRadians(mRotationX * Math.cos(Math.toRadians(radians)))) * mCarrouselR;
            float rotationZ_y = -(float) Math.sin(Math.toRadians(-mRotationZ)) * x0;
            float rotationZ_x = (((float) Math.cos(Math.toRadians(-mRotationZ)) * x0) - x0);
            if (mCarrouselViews.get(i) != null) {
                mCarrouselViews.get(i).setScaleX(scale0);
                mCarrouselViews.get(i).setScaleY(scale0);
                mCarrouselViews.get(i).setTranslationX(x0 + rotationZ_x);
                mCarrouselViews.get(i).setTranslationY(rotationX_y + rotationZ_y);
            }
        }
        List<View> arrayViewList = new ArrayList<>(mCarrouselViews);
        sortList(arrayViewList);
        postInvalidate();
    }

    /**
     * 排序
     * 對子View 排序，然后根据变化选中是否重绘,这样是为了实现view 在显示的时候来控制当前要显示的是哪三个view，可以改变排序看下效果
     *
     * @param list
     */
    @SuppressWarnings("unchecked")
    private <T> void sortList(List<View> list) {
        @SuppressWarnings("rawtypes")
        Comparator comparator = new SortComparator();
        T[] array = list.toArray((T[]) new Object[list.size()]);
        Arrays.sort(array, comparator);
        int i = 0;
        ListIterator<T> it = (ListIterator<T>) list.listIterator();
        while (it.hasNext()) {
            it.next();
            it.set(array[i++]);
        }
        for (int j = 0; j < list.size(); j++) {
            list.get(j).bringToFront();
        }
    }

    /**
     * 筛选器
     */
    private static class SortComparator implements Comparator<View> {
        @Override
        public int compare(View o1, View o2) {
            return (int) (1000 * o1.getScaleX() - 1000 * o2.getScaleX());
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshLayout();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(msgWhat, mRotationTime);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            checkChildView();
            startAnimationR();
        }
    }

    /**
     * 旋转木马半径打开动画
     */
    public void startAnimationR() {
        startAnimationR(1f, mCarrouselR);
    }

    /**
     * 半径扩散、收缩动画 根据设置半径来实现
     */
    public void startAnimationR(float from, float to) {
        mAnimationR = ValueAnimator.ofFloat(from, to);
        mAnimationR.addUpdateListener(valueAnimator -> {
            mCarrouselR = (Float) valueAnimator.getAnimatedValue();
            refreshLayout();
        });
        mAnimationR.setInterpolator(new DecelerateInterpolator());
        mAnimationR.setDuration(2000);
        mAnimationR.start();
    }

    public void checkChildView() {
        //先清空views里边可能存在的view防止重复
        if (mCarrouselViews == null) {
            return;
        }
        mCarrouselViews.clear();
        final int count = getChildCount(); //获取子View的个数
        viewCount = count;
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i); //获取指定的子view
            mCarrouselViews.add(view);
/*
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCarrouselItemClickListener != null) {
                        mOnCarrouselItemClickListener.onItemClick(view, position);

                    }
                }
            });
*/

        }

    }

    /**
     * 动画旋转
     */
    private void startAnimRotation(float resultAngle) {
        if (mAngle == resultAngle) {
            return;
        }
        restAnimator = ValueAnimator.ofFloat(mAngle, resultAngle);
        //设置旋转匀速插值器
        restAnimator.setInterpolator(new LinearInterpolator());
        restAnimator.setDuration(300);
        restAnimator.addUpdateListener(animation -> {

            mAngle = (Float) animation.getAnimatedValue();
            refreshLayout();

        });
        restAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                selectItem = calculateItem();
                if (selectItem < 0) {
                    selectItem = viewCount + selectItem;
                }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        restAnimator.start();
    }

    /**
     * 通过角度计算是第几个item
     */
    private int calculateItem() {
        return (int) (mAngle / (360 / viewCount)) % viewCount;
    }


    /**
     * 触摸时停止自动加载
     */
    public void setCanAutoRotation(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoRotation();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resumeAutoRotation();
                break;
        }
    }

    /**
     * 停止自动加载
     */
    public void stopAutoRotation() {
        if (handler != null) {
            handler.removeMessages(msgWhat);
        }
    }


    /**
     * 停止自动加载
     */
    public void destroyAutoRotation() {

        if (handler != null) {
            handler.removeMessages(msgWhat);
        }
        handler = null;

    }

    /**
     * 从新启动自动加载
     */
    public void resumeAutoRotation() {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(msgWhat, mRotationTime);
        }
    }

    /**
     * 获取所有的view
     */
    public List<View> getViews() {
        return mCarrouselViews;
    }

    /**
     * 获取角度
     */
    public float getAngle() {
        return mAngle;
    }


    /**
     * 设置角度
     */
    public void setAngle(float angle) {
        this.mAngle = angle;
    }

    /**
     * 获取距离
     */
    public float getDistance() {
        return mDistance;
    }

    /**
     * 设置距离
     */
    public void setDistance(float distance) {
        this.mDistance = distance;
    }

    /**
     * 获取半径
     */
    public float getR() {
        return mCarrouselR;
    }

    /**
     * 获取选择是第几个item
     */
    public int getSelectItem() {
        return selectItem;
    }


    /**
     * 设置半径
     */
    public CarrouselLayout setR(float r) {
        this.mCarrouselR = r;
        mDistance = 2f * r;
        return this;
    }


    public void createXAnimation(int from, int to, boolean start) {
        if (xAnimation != null) if (xAnimation.isRunning()) xAnimation.cancel();
        xAnimation = ValueAnimator.ofInt(from, to);
        xAnimation.addUpdateListener(animation -> {
            mRotationX = (Integer) animation.getAnimatedValue();
            refreshLayout();
        });
        xAnimation.setInterpolator(new LinearInterpolator());
        xAnimation.setDuration(2000);
        if (start) xAnimation.start();
    }


    public ValueAnimator createZAnimation(int from, int to, boolean start) {
        if (zAnimation != null) if (zAnimation.isRunning()) zAnimation.cancel();
        zAnimation = ValueAnimator.ofInt(from, to);
        zAnimation.addUpdateListener(animation -> {
            mRotationZ = (Integer) animation.getAnimatedValue();
            refreshLayout();
        });
        zAnimation.setInterpolator(new LinearInterpolator());
        zAnimation.setDuration(2000);
        if (start) zAnimation.start();
        return zAnimation;
    }

    public CarrouselLayout setRotationX(int mRotationX) {
        this.mRotationX = mRotationX;
        return this;
    }

    public CarrouselLayout setRotationZ(int mRotationZ) {
        this.mRotationZ = mRotationZ;
        return this;
    }

    public float getRotationX() {
        return mRotationX;
    }

    public int getRotationZ() {
        return mRotationZ;
    }

    public ValueAnimator getRestAnimator() {
        return restAnimator;
    }

    public ValueAnimator getAnimationR() {
        return mAnimationR;
    }

    public void setAnimationZ(ValueAnimator zAnimation) {
        this.zAnimation = zAnimation;
    }

    public ValueAnimator getAnimationZ() {
        return zAnimation;
    }

    public void setAnimationX(ValueAnimator xAnimation) {
        this.xAnimation = xAnimation;
    }

    public ValueAnimator getAnimationX() {
        return xAnimation;
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //清除所有mMsgWhat的消息
            try {
                removeMessages(msgWhat);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //旋转通知
            onRotating(CarrouselRotateDirection.anticlockwise);
            if (handler != null) {
                Message message = new Message();
                message.what = msgWhat;
                this.sendMessageDelayed(message, mRotationTime);
            }

        }
    };

    public void onRotating(CarrouselRotateDirection rotateDirection) {//接受到需要旋转指令
        try {
            if (viewCount != 0) {//判断自动滑动从那边开始
                int perAngle = 0;
                switch (rotateDirection) {
                    case clockwise:
                        perAngle = 360 / viewCount;
                        break;
                    case anticlockwise:
                        perAngle = -360 / viewCount;
                        break;
                }
                if (mAngle == 360) {
                    mAngle = 0f;
                }
                startAnimRotation(mAngle + perAngle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
