package com.tftechsz.family.adapter;

import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.netease.nim.uikit.common.UIUtils;
import com.tftechsz.family.BR;
import com.tftechsz.family.R;
import com.tftechsz.family.databinding.ItemFamilyMemberSearchBinding;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.family.adapter
 * 描 述 : 家族成员搜索adapter
 */
public class FamilyMemberSearchAdapter extends BaseQuickAdapter<FamilyMemberDto, DataBindBaseViewHolder> {
    private final UserProviderService service;

    public FamilyMemberSearchAdapter() {
        super(R.layout.item_family_member_search);
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder holder, FamilyMemberDto item) {
        ItemFamilyMemberSearchBinding binding = (ItemFamilyMemberSearchBinding) holder.getBind();
        binding.setVariable(BR.item, item);
        binding.executePendingBindings();
        CommonUtil.setSexAndAge(getContext(), item.sex, item.age, binding.tvSex);
        binding.setIsMine(service.getUserId() == item.user_id);
        UIUtils.setFamilyTitle(binding.tvType, item.role_id);
        GlideUtils.loadRoundImage(getContext(), binding.ivAvatar, item.icon, 100);
        binding.tvActiveTime.setVisibility(TextUtils.isEmpty(item.active_time) ? View.GONE : View.VISIBLE);
    }
}
