package com.tftechsz.mine.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class RechargeAdapter extends BaseQuickAdapter<RechargeDto, BaseViewHolder> {

    public int selectedIndex = -1;

    public RechargeAdapter() {
        super(R.layout.item_charge_list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, RechargeDto item) {
        helper.setText(R.id.tv_coin, item.coin)
                .setText(R.id.tv_coin_title, item.title)
                .setText(R.id.tv_coin_desc, item.desc)
                .setText(R.id.tv_rmb, "¥" + item.rmb)
                .setGone(R.id.tv_coin_title, TextUtils.isEmpty(item.title))
                .setGone(R.id.tv_coin_desc, TextUtils.isEmpty(item.desc));

        GlideUtils.loadImage(getContext(), helper.getView(R.id.iv_icon), item.image);
    }
}
