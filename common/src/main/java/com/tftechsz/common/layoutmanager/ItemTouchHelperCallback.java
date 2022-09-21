package com.tftechsz.common.layoutmanager;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.netease.nim.uikit.common.UserInfo;

import java.util.List;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private BaseQuickAdapter mAdapter;

    public ItemTouchHelperCallback(BaseQuickAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT );
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Object remove = mAdapter.getData().remove(viewHolder.getLayoutPosition());
        if(viewHolder.getLayoutPosition() == 0) {
            mAdapter.getData().add(remove);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //计算移动距离
        float distance = (float) Math.hypot(dX, dY);
        float maxDistance = recyclerView.getWidth() / 2f;

        //比例
        float fraction = distance / maxDistance;
        if (fraction > 1) {
            fraction = 1;
        }
        //为每个child执行动画
        int count = recyclerView.getChildCount();

        for (int i = 0; i < count; i++) {
            //获取的view从下层到上层
            View view = recyclerView.getChildAt(i);

            int level = CardConfig.SHOW_MAX_COUNT - i - 1;
            //level范围（CardConfig.SHOW_MAX_COUNT-1）-0，每个child最大只移动一个CardConfig.TRANSLATION_Y和放大CardConfig.SCALE

            if (level == CardConfig.SHOW_MAX_COUNT - 1) { // 最下层的不动和最后第二层重叠
                view.setTranslationY(CardConfig.TRANSLATION_Y * (level - 1));
                view.setScaleX(1 - CardConfig.SCALE * (level - 1));
                view.setScaleY(1 - CardConfig.SCALE * (level - 1));
            } else if (level > 0) {
                view.setTranslationY(level * CardConfig.TRANSLATION_Y - fraction * CardConfig.TRANSLATION_Y);
                float scale = 1 - level * CardConfig.SCALE + fraction * CardConfig.SCALE;
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.3f;
    }
}
