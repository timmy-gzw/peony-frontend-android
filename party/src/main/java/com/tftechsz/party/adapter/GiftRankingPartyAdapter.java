package com.tftechsz.party.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.PartyRankBean;

/**
 * 中奖礼物
 */
public class GiftRankingPartyAdapter extends BaseQuickAdapter<PartyRankBean.ListDTO.GiftListDTO, BaseViewHolder> {


    public GiftRankingPartyAdapter() {
        super(R.layout.item_ranking_pop_gift);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PartyRankBean.ListDTO.GiftListDTO item) {
        GlideUtils.loadImage(helper.itemView.getContext(), helper.getView(R.id.img_irpg), item.gift_image);
        helper.setText(R.id.tv_irpg_icon, "x" + item.num);
    }
}
