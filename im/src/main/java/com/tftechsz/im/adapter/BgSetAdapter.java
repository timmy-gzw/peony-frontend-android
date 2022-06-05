package com.tftechsz.im.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.im.BR;
import com.tftechsz.im.R;
import com.tftechsz.im.databinding.ItemBgSetBinding;
import com.tftechsz.im.model.BgSetBean;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 包 名 : com.tftechsz.im.adapter
 * 描 述 : TODO
 */
public class BgSetAdapter extends BaseQuickAdapter<BgSetBean, DataBindBaseViewHolder> {
    public BgSetAdapter() {
        super(R.layout.item_bg_set);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder helper, BgSetBean bean) {
        ItemBgSetBinding bind = (ItemBgSetBinding) helper.getBind();
        bind.setVariable(BR.item, bean);
        bind.executePendingBindings();
        GlideUtils.loadRouteImage(getContext(), bind.bg, bean.cover);
    }
}
