package com.tftechsz.party.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.dto.PartyInfoDto;

import org.jetbrains.annotations.NotNull;

public class PartyAdapter extends BaseQuickAdapter<PartyInfoDto, BaseViewHolder> {
    public PartyAdapter() {
        super(R.layout.item_party);
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PartyInfoDto item) {
        GlideUtils.loadTopRoundImageError(getContext(), baseViewHolder.getView(R.id.iv_thumb), item.getIcon());
        ImageView ivRecommend =  baseViewHolder.getView(R.id.iv_recommend);
        ivRecommend.setImageResource(R.drawable.bg_trans);
        //推荐图片
        if (!TextUtils.isEmpty(item.getRecommend_icon()))
            GlideUtils.loadImage(getContext(), ivRecommend, item.getRecommend_icon());
        if (!TextUtils.isEmpty(item.getClassify_icon()))
            GlideUtils.loadImage(getContext(), baseViewHolder.getView(R.id.iv_classify), item.getClassify_icon());
        baseViewHolder.setText(R.id.tv_name, item.getRoom_name());
        baseViewHolder.setText(R.id.tv_hot, item.getHeat());
    }
}
