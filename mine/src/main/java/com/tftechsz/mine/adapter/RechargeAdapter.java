package com.tftechsz.mine.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.entity.RechargeDto;
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
        helper.setText(R.id.tv_coin, item.coin.replace(getContext().getResources().getString(R.string.coin), ""))
                .setText(R.id.tv_desc, item.title)
                .setText(R.id.tv_rmb, "¥" + item.rmb)
                .setGone(R.id.tv_desc, TextUtils.isEmpty(item.title));

        helper.itemView.setSelected(selectedIndex == helper.getLayoutPosition());
    }
}
