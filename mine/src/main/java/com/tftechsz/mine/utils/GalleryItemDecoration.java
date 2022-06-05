package com.tftechsz.mine.utils;

import android.graphics.Rect;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 包 名 : com.tftechsz.mine.utils
 * 描 述 : TODO
 */
public class GalleryItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 第一张图片的左边距
     */
    private int mLeftPageVisibleWidth;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        //计算一下第一中图片距离屏幕左边的距离：(屏幕的宽度-item的宽度)/2。其中item的宽度=实际ImageView的宽度+margin。
        //我这里设置的ImageView的宽度为100+margin=110
        if (mLeftPageVisibleWidth == 0) {
            //计算一次就好了
            mLeftPageVisibleWidth = ConvertUtils.px2dp(getScreenWidth() - ConvertUtils.dp2px(100)) / 2;
        }

        //获取当前Item的position
        int position = parent.getChildAdapterPosition(view);
        //获得Item的数量
        int itemCount = parent.getAdapter() == null ? 0 : parent.getAdapter().getItemCount();
        int leftMargin;
        /*
         * 自定义默认的Item的边距
         */
        int pageMargin = 0;
        if (position == 0) {
            leftMargin = ConvertUtils.dp2px(mLeftPageVisibleWidth);
        } else {
            leftMargin = ConvertUtils.dp2px(pageMargin);
        }
        int rightMargin;
        if (position == itemCount - 1) {
            rightMargin = ConvertUtils.dp2px(mLeftPageVisibleWidth);
        } else {
            rightMargin = ConvertUtils.dp2px(pageMargin);
        }
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();

        //10,10分别是item到上下的margin
        layoutParams.setMargins(leftMargin, pageMargin, rightMargin, pageMargin);
        view.setLayoutParams(layoutParams);

        super.getItemOffsets(outRect, view, parent, state);
    }

    /**
     * 获取屏幕的宽度
     *
     * @return :
     */
    public static int getScreenWidth() {
        return ScreenUtils.getScreenWidth();
    }


}
