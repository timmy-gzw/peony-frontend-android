package com.tftechsz.party.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.PartyTurntableBean;

/**
 * 抽奖记录-
 */
public class GiftRecordPartyAdapter extends BaseQuickAdapter<PartyTurntableBean.RewardDTO, BaseViewHolder> {


    public GiftRecordPartyAdapter() {
        super(R.layout.item_ranking_pop_gift);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PartyTurntableBean.RewardDTO item) {
        GlideUtils.loadImageNew(helper.itemView.getContext(), helper.getView(R.id.img_irpg), item.gift_image);
        if (item.num != null) {
            helper.setText(R.id.tv_irpg_icon, "x" + item.num.toString());
        }

    }
}
