package com.tftechsz.family.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.family.adapter
 * 描 述 : TODO
 */
public class FamilyTaskAdapter extends BaseQuickAdapter<FamilyInfoDto.Task, BaseViewHolder> {
    public FamilyTaskAdapter() {
        super(R.layout.item_family_task);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FamilyInfoDto.Task item) {
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.icon), item.icon);
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_coin), item.reward_icon);
        helper.setText(R.id.title, item.title);
        helper.setText(R.id.coin, item.coin);
        TextView status = helper.getView(R.id.status);
        switch (item.status) { //0-进行中 1-待发放 2-已完成
            case 0:
                status.setText("进行中");
                status.setBackgroundResource(R.drawable.family_task_bg_status2);
                status.setTextColor(Color.parseColor("#9B7414"));
                break;

            case 1:
                status.setBackgroundResource(R.drawable.family_task_bg_status1);
                status.setTextColor(Utils.getColor(R.color.red));
                status.setText("待发放");
                break;

            case 2:
                status.setText("已完成");
                status.setBackgroundResource(R.drawable.shape_bg_f9f9f9_radius);
                status.setTextColor(Utils.getColor(R.color.color_cc));
                break;
        }

    }
}
