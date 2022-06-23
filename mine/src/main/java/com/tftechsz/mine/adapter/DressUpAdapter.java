package com.tftechsz.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemDressUpBinding;
import com.tftechsz.mine.entity.DressUpBean;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class DressUpAdapter extends BaseQuickAdapter<DressUpBean, DataBindBaseViewHolder> {
    public DressUpAdapter() {
        super(R.layout.item_dress_up);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder vh, DressUpBean bean) {
        ItemDressUpBinding bind = (ItemDressUpBinding) vh.getBind();
        bind.setItem(bean);
        bind.executePendingBindings();

    }
}
