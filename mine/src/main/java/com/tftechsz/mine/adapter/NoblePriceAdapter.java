package com.tftechsz.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.mine.BR;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemNoblePriceBinding;
import com.tftechsz.mine.entity.NobleBean;

import androidx.annotation.NonNull;

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
        mBind.setVariable(BR.item, bean);
        mBind.executePendingBindings();
    }
}
