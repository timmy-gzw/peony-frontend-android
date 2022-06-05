package com.tftechsz.im.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.CoupleBagDto;
import com.tftechsz.common.utils.GlideUtils;

public class CoupleBagAdapter extends BaseQuickAdapter<CoupleBagDto, BaseViewHolder> {
    public CoupleBagAdapter() {
        super(R.layout.item_couple_bag);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CoupleBagDto coupleBagDto) {
        GlideUtils.loadImage(getContext(), baseViewHolder.findView(R.id.iv_pic), coupleBagDto.icon);
        baseViewHolder.setVisible(R.id.iv_pic_time, !TextUtils.isEmpty(coupleBagDto.activity_icon));
        if (!TextUtils.isEmpty(coupleBagDto.activity_icon))
            GlideUtils.loadImage(getContext(), baseViewHolder.findView(R.id.iv_pic_time), coupleBagDto.activity_icon);
    }
}
