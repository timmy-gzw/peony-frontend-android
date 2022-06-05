package com.tftechsz.im.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.LiveHomeListDto;

import org.jetbrains.annotations.NotNull;

public class LiveHomeAdapter extends BaseQuickAdapter<LiveHomeListDto, BaseViewHolder> {

    public LiveHomeAdapter() {
        super(R.layout.item_live_home);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LiveHomeListDto item) {
        baseViewHolder.setText(R.id.tv_content, item.room_name);

    }
}
