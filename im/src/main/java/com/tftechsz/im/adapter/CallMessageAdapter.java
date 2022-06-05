package com.tftechsz.im.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.CallMessageDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CallMessageAdapter extends BaseQuickAdapter<CallMessageDto, BaseViewHolder> {

    public CallMessageAdapter(@Nullable List<CallMessageDto> data) {
        super(R.layout.item_call_message, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CallMessageDto item) {
        helper.setText(R.id.tv_gift_name,item.gift);
    }
}
