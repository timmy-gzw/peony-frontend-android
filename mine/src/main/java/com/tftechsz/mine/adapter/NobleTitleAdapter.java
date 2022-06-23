package com.tftechsz.mine.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemNobleTitleBinding;
import com.tftechsz.mine.entity.NobleBean;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class NobleTitleAdapter extends BaseQuickAdapter<NobleBean.GradeDTO, DataBindBaseViewHolder> {
    public NobleTitleAdapter() {
        super(R.layout.item_noble_title);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder holder, NobleBean.GradeDTO bean) {
        ItemNobleTitleBinding mBind = (ItemNobleTitleBinding) holder.getBind();
        mBind.setItem(bean);
        mBind.executePendingBindings();
    }
}
