package com.tftechsz.family.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.common.utils.GlideUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 排行
 */
public class FamilyRankAdapter extends BaseQuickAdapter<FamilyInfoDto, BaseViewHolder> {

    private int mFamilyId;

    public FamilyRankAdapter(@Nullable List<FamilyInfoDto> data, int familyId) {
        super(R.layout.item_family_rank, data);
        this.mFamilyId = familyId;
    }

    public void setFamilyId(int familyId) {
        mFamilyId = familyId;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FamilyInfoDto item) {
        TextView tvAdd = helper.getView(R.id.tv_add);
        GlideUtils.loadRoundImage(getContext(), helper.getView(R.id.iv_avatar), item.icon, 10);
        helper.setText(R.id.tv_name, item.family_name);
        helper.setText(R.id.tv_prestige, item.all_prestige);
        helper.setGone(R.id.tv_prestige, TextUtils.isEmpty(item.all_prestige));
        helper.setText(R.id.tv_number, item.user_count + "人");
        helper.setText(R.id.tv_desc, item.intro);
        helper.setVisible(R.id.tag_new, item.label_new == 1);
        //等级
        FamilyInfoDto.FamilyLevel level = item.family_level;
        if (null != level) {
            helper.setText(R.id.tv_level, level.level + ""); //等级
            GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_level), level.icon);
        }
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setEnabled(false);
        if (mFamilyId != 0) {  //有家族
            if (mFamilyId == item.family_id) {  //自己的家族
                tvAdd.setText("已加入");
                tvAdd.setTextColor(Color.parseColor("#999999"));
                tvAdd.setBackgroundResource(R.drawable.bg_gray_radius17);
            } else {
                tvAdd.setVisibility(View.GONE);
            }
        } else {
            if (item.is_apply_status == 1) {
                tvAdd.setText("已申请");
                tvAdd.setTextColor(Color.parseColor("#999999"));
                tvAdd.setBackgroundResource(R.drawable.bg_gray_radius17);
            } else {
                tvAdd.setText("加入");
                tvAdd.setBackgroundResource(R.drawable.bg_family_add);
                tvAdd.setTextColor(Color.parseColor("#CFB78A"));
                addChildClickViewIds(R.id.tv_add);
                tvAdd.setEnabled(true);
            }
        }

        helper.setGone(R.id.voice_family_bg, item.voice_online != 1);
        helper.setGone(R.id.voice_family_lottie, item.voice_online != 1);
    }


}
