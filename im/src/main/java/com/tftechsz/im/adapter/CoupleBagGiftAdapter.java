package com.tftechsz.im.adapter;


import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.CoupleBagDetailGiftDto;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 情侣任务礼包礼物
 */
public class CoupleBagGiftAdapter extends BaseQuickAdapter<CoupleBagDetailGiftDto, BaseViewHolder> {

    public CoupleBagGiftAdapter() {
        super(R.layout.item_couple_bag_gift);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CoupleBagDetailGiftDto item) {
        helper.setText(R.id.tv_type, item.type)
                .setText(R.id.tv_name, item.name)
                .setText(R.id.tv_price, item.coin + "金币");
        GlideUtils.loadImage(getContext(), helper.findView(R.id.iv_gift), item.icon);
        FrameLayout flGift = helper.findView(R.id.fl_gift);
        if (flGift != null) {
            ConstraintLayout.LayoutParams top = (ConstraintLayout.LayoutParams) flGift.getLayoutParams();
            if (getItemPosition(item) == 1 || getItemPosition(item) == 4) {
                top.topMargin = DensityUtils.dp2px(getContext(), getData().size() > 3 ? 13 : 20);
            } else {
                top.topMargin = DensityUtils.dp2px(getContext(), 0);
            }
            flGift.setLayoutParams(top);
        }
        ConstraintLayout clRoot = helper.findView(R.id.cl_root);
        if (clRoot != null) {
            GridLayoutManager.LayoutParams top = (GridLayoutManager.LayoutParams) clRoot.getLayoutParams();
            if (getItemPosition(item) >= 3) {
                top.topMargin = DensityUtils.dp2px(getContext(), -13);
            } else {
                top.topMargin = DensityUtils.dp2px(getContext(), 0);
            }
            clRoot.setLayoutParams(top);
        }
    }


}
