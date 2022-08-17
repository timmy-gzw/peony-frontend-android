package com.tftechsz.home.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.home.R;
import com.tftechsz.home.adapter.HomeMessageAdapter;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.entity.MessageInfo;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.VerticalLoopLayoutManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import static android.os.Looper.getMainLooper;

public class ScrollerForbidView extends LinearLayout {
    private final RecyclerView recyclerView;
    private float mLastRawx;
    private float mLastRawy;
    private boolean isDrag = false;
    private int mRootMeasuredWidth;
    private final int padding = DensityUtils.dp2px(BaseApplication.getInstance(), 16);
    private final static int LOOP_RECYCLER_VIEW_MSG = 1000;
    private final static int LOOP_INTERVAL = 3000;
    private VerticalLoopLayoutManager verticalLoopLayoutManager;
    private HomeMessageAdapter mAdapter;
    private int position = 0;
    private int targetItemPosition = 0;
    private int size;
    Handler myHandler = new Handler(getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == LOOP_RECYCLER_VIEW_MSG) {
                int firstItem;
                if (position == size - 1) {
                    position = 0;
                } else {
                    position++;
                }
                if (mAdapter.getData().size() > 1) {
                    firstItem = verticalLoopLayoutManager.findFirstVisibleItemPosition();
                    targetItemPosition = (firstItem + 1) % size;
                    recyclerView.smoothScrollToPosition(targetItemPosition);
                    myHandler.sendEmptyMessageDelayed(LOOP_RECYCLER_VIEW_MSG, LOOP_INTERVAL);
                }
            }
        }
    };

    public ScrollerForbidView(Context context) {
        this(context, null);
    }

    public ScrollerForbidView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.scroller_forbid_view, this);
        recyclerView = findViewById(R.id.rv_message_refresh);
        verticalLoopLayoutManager = new VerticalLoopLayoutManager(getContext());
        recyclerView.setLayoutManager(verticalLoopLayoutManager);
        mAdapter = new HomeMessageAdapter();
        recyclerView.setAdapter(mAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.requestDisallowInterceptTouchEvent(true);
    }


    public void loadMessage(List<MessageInfo> messageInfoList) {
        stopLoop();
        Utils.runOnUiThread(() -> {
            mAdapter.getData().clear();
            mAdapter.setList(messageInfoList);
            size = mAdapter.getData().size();
            if (size > 1) {
                myHandler.sendEmptyMessageDelayed(LOOP_RECYCLER_VIEW_MSG, LOOP_INTERVAL);
            }
            position = 0;
        });
    }


    public void startLoop() {
        stopLoop();
        if (size > 1 && myHandler != null) {
            myHandler.sendEmptyMessageDelayed(LOOP_RECYCLER_VIEW_MSG, LOOP_INTERVAL);
        }
    }


    public void stopLoop() {
        if (myHandler != null) {
            myHandler.removeMessages(0);
            myHandler.removeMessages(LOOP_RECYCLER_VIEW_MSG);
            myHandler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //当前手指的坐标
        float mRawx = ev.getRawX();
        float mRawy = ev.getRawY();
        ViewGroup mViewGroup = (ViewGroup) getParent();

        switch (ev.getAction()) {
            //手指按下
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                isDrag = false;
                //记录按下的位置
                mLastRawx = mRawx;
                mLastRawy = mRawy;
                if (mViewGroup != null) {
                    int[] location = new int[2];
                    mViewGroup.getLocationInWindow(location);
                    //获取父布局的宽度
                    mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                }
                break;
            //手指滑动
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (mViewGroup != null) {
                    int[] location = new int[2];
                    mViewGroup.getLocationInWindow(location);
                    //获取父布局的高度
                    int mRootMeasuredHeight = mViewGroup.getMeasuredHeight();
                    mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                    //获取父布局顶点的坐标
                    int rootTopy = location[1];
                    if (mRawx >= 0 && mRawx <= mRootMeasuredWidth && mRawy >= DensityUtils.dp2px(BaseApplication.getInstance(), 40) && mRawy <= (mRootMeasuredHeight - rootTopy)) {
                        //手指X轴滑动距离
                        float differenceValuex = mRawx - mLastRawx;
                        //手指Y轴滑动距离
                        float differenceValuey = mRawy - mLastRawy;
                        //判断是否为拖动操作
                        if (!isDrag) {
                            isDrag = !(Math.sqrt(differenceValuex * differenceValuex + differenceValuey * differenceValuey) < 2);
                        }
                        //获取手指按下的距离与控件本身X轴的距离
                        float ownx = getX();
                        //获取手指按下的距离与控件本身Y轴的距离
                        float owny = getY();
                        //理论中X轴拖动的距离
                        float endx = ownx + differenceValuex;
                        //理论中Y轴拖动的距离
                        float endy = owny + differenceValuey;
                        //X轴可以拖动的最大距离
                        float maxx = mRootMeasuredWidth - getWidth();
                        //Y轴可以拖动的最大距离
                        float maxy = mRootMeasuredHeight - getHeight();
                        //X轴边界限制
                        endx = endx < 0 ? 0 : Math.min(endx, maxx);
                        //Y轴边界限制
                        endy = endy < 0 ? 0 : Math.min(endy, maxy);
                        //开始移动
                        setX(endx);
                        setY(endy);
                        //记录位置
                        mLastRawx = mRawx;
                        mLastRawy = mRawy;
                    }
                }
                break;
            //手指离开
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                Log.e(this.getClass().getSimpleName(), "onTouchEvent: "+"--isDrag:"+isDrag+" --mAdapter.getData().size():"+mAdapter.getData().size()+" --targetItemPosition:"+targetItemPosition);
                //恢复按压效果
                if (!isDrag) {
                    stopLoop();
                    Utils.runOnUiThread(() -> {
                        try {
                            if (mAdapter.getData().size() > 0 && mAdapter.getData().size() > targetItemPosition) {
                                ARouterUtils.toChatP2PActivity(mAdapter.getData().get(targetItemPosition).contactId, NimUIKit.getCommonP2PSessionCustomization(), null);
                                mAdapter.getData().remove(targetItemPosition);
                                mAdapter.notifyItemRemoved(targetItemPosition);
                                if (targetItemPosition != 0)
                                    targetItemPosition--;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                setPressed(false);
                this.animate()
                        .setInterpolator(new BounceInterpolator())
                        .setDuration(500)
                        .x(mRootMeasuredWidth - getWidth() - padding)
                        .start();
                break;
            default:
        }
        //是否拦截事件
        return isDrag || super.onTouchEvent(ev);
    }
}
