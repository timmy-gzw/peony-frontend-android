package com.tftechsz.mine.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.GiftDto;

import java.util.List;

/**
 * 礼物
 */
public class GiftAdapter extends BaseQuickAdapter<GiftDto, BaseViewHolder> {
    public GiftAdapter(@Nullable List<GiftDto> data) {
        super(R.layout.mine_item_gift, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GiftDto item) {
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_gift), item.image);
        helper.setText(R.id.tv_gift_name, item.title);
    }
}
