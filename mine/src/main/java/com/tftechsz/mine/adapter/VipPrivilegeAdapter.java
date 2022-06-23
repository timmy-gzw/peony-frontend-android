package com.tftechsz.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemVipPrivilegeBinding;
import com.tftechsz.mine.entity.VipPrivilegeBean;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class VipPrivilegeAdapter extends BaseQuickAdapter<VipPrivilegeBean, DataBindBaseViewHolder> {

    public VipPrivilegeAdapter() {
        super(R.layout.item_vip_privilege);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder vh, VipPrivilegeBean bean) {
        ItemVipPrivilegeBinding mBind = (ItemVipPrivilegeBinding) vh.getBind();
        mBind.setItem(bean);
        mBind.executePendingBindings();
    }
}
