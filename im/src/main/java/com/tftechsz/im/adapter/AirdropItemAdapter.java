package com.tftechsz.im.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class AirdropItemAdapter extends BaseQuickAdapter<GiftDto, BaseViewHolder> {


    public AirdropItemAdapter() {
        super(R.layout.item_airdrop_item);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, GiftDto item) {
        GlideUtils.loadRouteImage(getContext(), baseViewHolder.getView(R.id.iv_gift), item.image);
        baseViewHolder.setText(R.id.tv_num, "x" + item.num);

    }
}
