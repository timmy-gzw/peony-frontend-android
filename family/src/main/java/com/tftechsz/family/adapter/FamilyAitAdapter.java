package com.tftechsz.family.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import java.util.List;

public class FamilyAitAdapter extends BaseQuickAdapter<FamilyMemberDto, BaseViewHolder> {

    public FamilyAitAdapter(@Nullable List<FamilyMemberDto> data) {
        super(R.layout.item_family_ait, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FamilyMemberDto item) {
        TextView tvContent = helper.getView(R.id.tv_content);
        helper.setText(R.id.tv_name,item.nickname);
        CommonUtil.setSexAndAge(getContext(), item.sex, item.age, helper.getView(R.id.tv_sex));
        String text = TimeUtil.getTimeShowString(item.created_at * 1000, false);
        helper.setText(R.id.tv_time,text);
        GlideUtils.loadRoundImage(getContext(), helper.getView(R.id.iv_avatar), item.icon, 6);
        tvContent.setText(item.content);
    }

}
