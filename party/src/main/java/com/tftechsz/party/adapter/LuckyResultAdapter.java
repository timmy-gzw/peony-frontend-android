package com.tftechsz.party.adapter;


import android.graphics.Color;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.WheelResultBean;

/**
 * 中奖礼物
 */
public class LuckyResultAdapter extends BaseQuickAdapter<WheelResultBean.RewardBean, BaseViewHolder> {

    private int count;

    public LuckyResultAdapter(int count ) {
        super(R.layout.adapter_luckyresult);
        this.count = count;
     }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, WheelResultBean.RewardBean item) {
        GlideUtils.loadImage(helper.itemView.getContext(), helper.getView(R.id.img_gift_adapter), item.gift_image);
        int position = getItemPosition(item);
        if (position < count) {
            if (position == count - 1) {
                helper.setVisible(R.id.view_al_line, false);
            } else {
                helper.setVisible(R.id.view_al_line, true);
            }
        } else {
            if (position % count == 1 && position != 0) {
                helper.setVisible(R.id.view_al_line, false);
            } else {
                helper.setVisible(R.id.view_al_line, true);
            }
        }
//最后一个隐藏
        if (position == getItemCount() - 1) {
            helper.setVisible(R.id.view_al_line, false);
        }
        helper.setText(R.id.tv_al_text, "x" + item.num);

        helper.setBackgroundColor(R.id.view_al_line, Color.parseColor("#245febee"));
        helper.setTextColor(R.id.tv_al_text,  Color.parseColor("#ff6aecf5"));

    }
}
