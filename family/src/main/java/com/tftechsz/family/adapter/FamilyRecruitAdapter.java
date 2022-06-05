package com.tftechsz.family.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.family.BR;
import com.tftechsz.family.R;
import com.tftechsz.family.databinding.ItemFamilyRecruitBinding;
import com.tftechsz.family.entity.dto.FamilyRecruitDto;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.family.adapter
 * 描 述 : 家族招募adapter
 */
public class FamilyRecruitAdapter extends BaseQuickAdapter<FamilyRecruitDto, DataBindBaseViewHolder> {
    public FamilyRecruitAdapter() {
        super(R.layout.item_family_recruit);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder helper, FamilyRecruitDto bean) {
        ItemFamilyRecruitBinding mBinding = (ItemFamilyRecruitBinding) helper.getBind();
        mBinding.setVariable(BR.item, bean);
        mBinding.setVariable(BR.itemLevel, bean.family_level);
        mBinding.executePendingBindings();

        GlideUtils.loadRoundImageRadius(getContext(), mBinding.icon, bean.icon);
        GlideUtils.loadImage(getContext(), mBinding.ivLevel, bean.family_level.icon);
        mBinding.driver.setVisibility(helper.getLayoutPosition() + 1 == getItemCount() ? View.GONE : View.VISIBLE);

    }
}
