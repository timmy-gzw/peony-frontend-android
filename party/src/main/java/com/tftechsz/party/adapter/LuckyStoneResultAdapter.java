package com.tftechsz.party.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.TurntableStartBeforeBean;

/**
 * 新增转盘自动购买气泡dialog
 */
public class LuckyStoneResultAdapter extends BaseQuickAdapter<TurntableStartBeforeBean.BuyBean, BaseViewHolder> {


    public LuckyStoneResultAdapter() {
        super(R.layout.adapter_lucky_stone_result);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TurntableStartBeforeBean.BuyBean item) {
        GlideUtils.loadImage(helper.itemView.getContext(), helper.getView(R.id.img_gift_adapter_lucky_stone), item.image);

    }
}
