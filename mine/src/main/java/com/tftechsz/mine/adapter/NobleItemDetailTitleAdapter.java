package com.tftechsz.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.mine.BR;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemNobleItemDetailTitleBinding;
import com.tftechsz.mine.entity.NobleBean;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class NobleItemDetailTitleAdapter extends BaseQuickAdapter<NobleBean.GradeDTO, DataBindBaseViewHolder> {
    public NobleItemDetailTitleAdapter() {
        super(R.layout.item_noble_item_detail_title);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder helper, NobleBean.GradeDTO bean) {
        ItemNobleItemDetailTitleBinding bind = (ItemNobleItemDetailTitleBinding) helper.getBind();
        bind.setVariable(BR.item, bean);
        bind.executePendingBindings();
    }

}
