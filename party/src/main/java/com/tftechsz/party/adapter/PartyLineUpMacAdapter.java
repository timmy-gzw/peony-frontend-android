package com.tftechsz.party.adapter;


import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.QueuePartyListBean;

/**
 * 麦序列表
 */
public class PartyLineUpMacAdapter extends BaseQuickAdapter<QueuePartyListBean.QueueInnerBean, BaseViewHolder> {

    //管理员可以同意上麦
    private boolean mFlagIsAni;
    private IListenerUp iListenerUp;
    private int userId;

    public PartyLineUpMacAdapter(boolean flagIsAni, IListenerUp iListenerUp, int userId) {
        super(R.layout.item_lineup_adapter);
        this.mFlagIsAni = flagIsAni;
        this.iListenerUp = iListenerUp;
        this.userId = userId;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, QueuePartyListBean.QueueInnerBean item) {
        if (mFlagIsAni) {
            helper.getView(R.id.tv_click_cancel_sequence).setVisibility(View.GONE);
            helper.getView(R.id.tv_click_up_sequence).setVisibility(View.VISIBLE);
        } else {
            if (userId == item.user_id) {//自己可以取消
                helper.getView(R.id.tv_click_cancel_sequence).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.tv_click_cancel_sequence).setVisibility(View.GONE);
            }

            helper.getView(R.id.tv_click_up_sequence).setVisibility(View.GONE);
        }

        helper.getView(R.id.tv_click_cancel_sequence).setOnClickListener(v -> iListenerUp.cancelMac(1));
        helper.getView(R.id.tv_click_up_sequence).setOnClickListener(v -> iListenerUp.up(item.user_id, item.id));
        helper.setText(R.id.tv_number_index, String.valueOf(getItemPosition(item) + 1));
        GlideUtils.loadRoundImage(helper.itemView.getContext(), helper.getView(R.id.img_ipm), item.icon, 100);
        helper.setText(R.id.tv_ipm_name, item.nickname);
        helper.setTextColor(R.id.tv_ipm_name, item.is_vip == 1 ? Color.parseColor("#F6508D") : Color.parseColor("#333333"));
        CommonUtil.setSexAndAge(helper.itemView.getContext(), item.sex, item.age, helper.getView(R.id.iv_sex));

    }

    public interface IListenerUp {
        void up(int userId, int id);

        void cancelMac(int userId);
    }

}
