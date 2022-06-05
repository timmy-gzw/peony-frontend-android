package com.tftechsz.family.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.family.BR;
import com.tftechsz.family.R;
import com.tftechsz.family.databinding.ItemFamilyBlackListBinding;
import com.tftechsz.family.entity.dto.FamilyBlackListDto;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.family.adapter
 * 描 述 : TODO
 */
public class FamilyBlackListAdapter extends BaseQuickAdapter<FamilyBlackListDto, DataBindBaseViewHolder> {
    public FamilyBlackListAdapter() {
        super(R.layout.item_family_black_list);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder helper, FamilyBlackListDto bean) {
        ItemFamilyBlackListBinding bind = (ItemFamilyBlackListBinding) helper.getBind();
        bind.setVariable(BR.item, bean);
        bind.executePendingBindings();

        CommonUtil.setSexAndAge(getContext(), bean.sex, bean.age, bind.tvSex);
        GlideUtils.loadRoundImageRadius(getContext(), bind.ivAvatar, bean.icon);
    }
}
