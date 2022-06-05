package com.tftechsz.family.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UIUtils;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FamilyDetailAdapter extends BaseQuickAdapter<FamilyMemberDto, BaseViewHolder> {
    public FamilyDetailAdapter(@Nullable List<FamilyMemberDto> data) {
        super(R.layout.item_family_detail, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FamilyMemberDto item) {
        GlideUtils.loadRoundImageRadius(getContext(), helper.getView(R.id.iv_avatar), item.icon);
        helper.setText(R.id.tv_name, item.nickname);
        CommonUtil.setSexAndAge(getContext(), item.sex, item.age, helper.getView(R.id.iv_sex));
        TextView tvType = helper.getView(R.id.tv_type);
        //id" => 4, "title" => "长老" ],[ "id" => 32, "title" => "副族长" ],[ "id" => 64, "title" => "族长" ],
        UIUtils.setFamilyTitle(tvType, item.role_id);
    }
}
