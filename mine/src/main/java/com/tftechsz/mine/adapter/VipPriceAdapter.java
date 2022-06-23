package com.tftechsz.mine.adapter;

import android.graphics.Paint;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemVipPriceBinding;
import com.tftechsz.mine.entity.VipPriceBean;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class VipPriceAdapter extends BaseQuickAdapter<VipPriceBean, DataBindBaseViewHolder> {

    public VipPriceAdapter() {
        super(R.layout.item_vip_price);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder helper, VipPriceBean bean) {
        ItemVipPriceBinding mBind = (ItemVipPriceBinding) helper.getBind();
        mBind.setItem(bean);
        mBind.executePendingBindings();
        if (bean.is_mid_line) {
            mBind.price3.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }
        /*if (bean.selIndex == helper.getLayoutPosition()) {
            mBind.root.setBackgroundResource(R.drawable.shape_member_price_bg_selected);
        } else {
            mBind.root.setBackgroundResource(R.drawable.shape_member_price_bg_normal);
        }*/
    }
}
