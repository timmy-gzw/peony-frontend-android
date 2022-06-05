package com.tftechsz.family.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UIUtils;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.utils.GlideUtils;

import androidx.annotation.NonNull;

public class MineFamilyAdapter extends BaseQuickAdapter<FamilyMemberDto, BaseViewHolder> {
    public MineFamilyAdapter() {
        super(R.layout.item_family_mine);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FamilyMemberDto item) {
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_avatar), item.icon);
        TextView tvType = helper.getView(R.id.tv_type);
        //id" => 4, "title" => "长老" ],[ "id" => 32, "title" => "副族长" ],[ "id" => 64, "title" => "族长" ],
        UIUtils.setFamilyTitle(tvType, item.role_id);
    }
}
