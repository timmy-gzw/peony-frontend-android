package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.tftechsz.party.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;


public class CustomNestedScrollView extends NestedScrollView {

    private View topView;
    private RecyclerView recyclerView;
    private int mMaxHeight = 0;
    private int topHeight;
    private int scrollY;

    public CustomNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public CustomNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topView = findViewById(R.id.banner_root);
        recyclerView = findViewById(R.id.rv_party);
        if (topView == null || recyclerView == null) {
            throw new IllegalStateException("topView or recyclerView must not null");
        }
    }

    /**
     * 设置最大高度
     */
    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //先测量recycler view的高度，和 ScrollViewNestedRecyclerView的高度一样，防止recycler view 自动布局把所有的item加载出来，失去复用
        recyclerView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getHeight() == 0 ? MeasureSpec.getSize(heightMeasureSpec) : getMeasuredHeight(), MeasureSpec.EXACTLY));
        ViewGroup.LayoutParams rcLp = recyclerView.getLayoutParams();
        if (mMaxHeight == 0) {
            rcLp.height = recyclerView.getMeasuredHeight();
        } else {
            rcLp.height = mMaxHeight;
        }
        recyclerView.setLayoutParams(rcLp);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        topHeight = topView.getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        topHeight = topView.getMeasuredHeight();
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (target instanceof NestedScrollingChild2) {
            //防止到顶部后一直调用
            if (dyConsumed == 0 && dyUnconsumed < 0 && type == ViewCompat.TYPE_NON_TOUCH && scrollY == 0) {
                ((NestedScrollingChild2) target).stopNestedScroll(type);
                return;
            }
        }
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (target instanceof NestedScrollingChild2) {
            //防止到顶部后一直调用
            if (dy < 0 && type == ViewCompat.TYPE_NON_TOUCH && scrollY == 0) {
                ((NestedScrollingChild2) target).stopNestedScroll(type);
                return;
            }
            if (scrollY < topHeight) {
                if (dy > 0) {//上滑
                    consumed[0] = dx;
                    consumed[1] = dy;
                    scrollBy(0, dy);
                } else {//下滑
                    if (!canScrollUp(target)) {//到达顶部
                        if (scrollY == 0) {
                            consumed[0] = dx;
                            consumed[1] = 0;
                            super.onNestedPreScroll(target, dx, dy, consumed, type);
                        } else {
                            consumed[0] = dx;
                            consumed[1] = dy;
                            scrollBy(0, dy);
                        }
                    }
                }
            } else {
                super.onNestedPreScroll(target, dx, dy, consumed, type);
            }
        } else {
            super.onNestedPreScroll(target, dx, dy, consumed, type);
        }
    }

    /**
     * 判断targetView是否还可以向上滚动
     *
     * @param targetView
     * @return
     */
    private boolean canScrollUp(View targetView) {
        if (targetView == null) {
            return false;
        }
        return targetView.canScrollVertically(-1);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        scrollY = t;
    }
}
