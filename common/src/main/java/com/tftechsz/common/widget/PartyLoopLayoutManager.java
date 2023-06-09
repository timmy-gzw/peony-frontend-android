package com.tftechsz.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PartyLoopLayoutManager extends LinearLayoutManager {

    public PartyLoopLayoutManager(Context context) {
        super(context);
    }

    public PartyLoopLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PartyLoopLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /*@Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
            detachAndScrapAttachedViews(recycler);
            if (state.isPreLayout() || getItemCount() <= 0) {
                return;
            }
            int heightOffset = getPaddingTop();
            for (int i = 0; i < getItemCount(); i++) {
                View childView = recycler.getViewForPosition(i);
                addView(childView);
                measureChildWithMargins(childView, 0, 0);
                int viewWidth = getDecoratedMeasuredWidth(childView);
                int viewHeight = getDecoratedMeasuredHeight(childView);
                int left = getPaddingLeft();
                int top = heightOffset;
                int right = left + viewWidth - getPaddingRight();
                int bottom = top + viewHeight;
                layoutDecoratedWithMargins(childView, left, top, right, bottom);
                heightOffset += viewHeight;
                //超出高度不需要布局
                if (heightOffset + getPaddingTop() + getPaddingBottom() > getHeight()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            //填充item view
            fillChildren(dy, recycler);
            //上下滑动偏移动画
            offsetChildrenVertical(-dy);
            //回收超出边界的view复用
            recyclerViews(dy, recycler);
            return dy;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dy;
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return false;
    }

    private void fillChildren(int dy, RecyclerView.Recycler recycler) {
        if (dy > 0) {
            scrollUp(recycler);
        } else {
            scrollDown(recycler);
        }
    }

    //上滑
    private void scrollUp(RecyclerView.Recycler recycler) {
        //获取最后一个可见item
        View anchorView = getChildAt(getChildCount() - 1);
        while (true) {
            if (anchorView == null) {
                break;
            }
            //最后一个可见item还没完全进入
            if (anchorView.getBottom() + getPaddingBottom() > getHeight()) {
                break;
            }
            //最后一个可见item完全进入，填充下一个item
            int position = getPosition(anchorView);
            View nextView;
            if (position == getItemCount() - 1) {
                nextView = recycler.getViewForPosition(0);
            } else {
                nextView = recycler.getViewForPosition(position + 1);
            }
            if (nextView == null) {
                break;
            }
            addView(nextView);
            measureChildWithMargins(nextView, 0, 0);
            int viewWidth = getDecoratedMeasuredWidth(nextView);
            int viewHeight = getDecoratedMeasuredHeight(nextView);
            int left = getPaddingLeft();
            int right = getPaddingLeft() + viewWidth - getPaddingRight();
            int top = anchorView.getBottom();
            int bottom = top + viewHeight;
            layoutDecoratedWithMargins(nextView, left, top, right, bottom);
            anchorView = nextView;
        }
    }

    //下滑
    private void scrollDown(RecyclerView.Recycler recycler) {
        View anchorView = getChildAt(0);
        while (true) {
            if (anchorView == null) {
                break;
            }
            if (anchorView.getTop() - getPaddingTop() < 0) {
                break;
            }
            int position = getPosition(anchorView);
            View preView;
            if (position == 0) {
                preView = recycler.getViewForPosition(getItemCount() - 1);
            } else {
                preView = recycler.getViewForPosition(position - 1);
            }
            if (preView == null) {
                break;
            }
            addView(preView, 0);
            measureChildWithMargins(preView, 0, 0);
            int viewWidth = getDecoratedMeasuredWidth(preView);
            int viewHeight = getDecoratedMeasuredHeight(preView);
            int left = getPaddingLeft();
            int right = getPaddingLeft() + viewWidth - getPaddingRight();
            int top = anchorView.getTop() - viewHeight;
            int bottom = anchorView.getTop();
            layoutDecoratedWithMargins(preView, left, top, right, bottom);
            anchorView = preView;
        }
    }

    private void recyclerViews(int dy, RecyclerView.Recycler recycler) {
        int itemCount = getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View childView = getChildAt(i);
            if (childView == null) {
                continue;
            }
            if (dy > 0) {
                if (childView.getBottom() - getPaddingTop() <= 0) {
                    removeAndRecycleView(childView, recycler);
                }
            } else {
                if (childView.getTop() - getPaddingBottom() >= getHeight()) {
                    removeAndRecycleView(childView, recycler);
                }
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        //目前每个item高度是一样的
        int curFirstVisiblePosition = -1;
        int itemHeight = 0;
        //获取第一个可见View;
        View anchorView = getChildAt(0);
        if (anchorView != null) {
            curFirstVisiblePosition = getPosition(anchorView);
            itemHeight = anchorView.getHeight();
        }
        int dy = -1;
        if (curFirstVisiblePosition != -1) {
            if (curFirstVisiblePosition != position) {
                if (curFirstVisiblePosition < position) {
                    dy = (position - curFirstVisiblePosition) * itemHeight;
                } else {
                    dy = (getItemCount() - curFirstVisiblePosition + position) * itemHeight;
                }
            }
        }
        recyclerView.smoothScrollBy(0, dy);
    }
}
