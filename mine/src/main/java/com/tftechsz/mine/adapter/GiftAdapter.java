package com.tftechsz.mine.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.GiftDto;

/**
 * 礼物
 */
public class GiftAdapter extends BaseQuickAdapter<GiftDto, BaseViewHolder> {

    public GiftAdapter() {
        super(R.layout.mine_item_gift);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GiftDto item) {
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_gift), item.image);
        helper.setText(R.id.tv_gift_name, item.title);
    }
}
