package com.tftechsz.mine.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemNoblePriceBinding;
import com.tftechsz.mine.entity.NobleBean;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class NoblePriceAdapter extends BaseQuickAdapter<NobleBean.PriceDTO, DataBindBaseViewHolder> {
    public NoblePriceAdapter() {
        super(R.layout.item_noble_price);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder helper, NobleBean.PriceDTO bean) {
        ItemNoblePriceBinding mBind = (ItemNoblePriceBinding) helper.getBind();
        mBind.setItem(bean);
        mBind.executePendingBindings();
    }
}
