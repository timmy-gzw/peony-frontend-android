package com.tftechsz.party.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.PartyManagerListBean;

import org.jetbrains.annotations.NotNull;

public class PartyManagerAdapter extends BaseQuickAdapter<PartyManagerListBean.ListDTO, BaseViewHolder> {
    /**
     * 0房管，1 拉黑，2踢出，3禁言
     */
    public int tag;
    public IClickCancelData iClickCancelData;

    public PartyManagerAdapter(IClickCancelData iClickCancelData) {
        super(R.layout.item_party_manager);
        this.iClickCancelData = iClickCancelData;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PartyManagerListBean.ListDTO s) {
        if (tag == 0) {
            baseViewHolder.getView(R.id.tv_status_ipm).setVisibility(View.GONE);
            baseViewHolder.getView(R.id.tv_time).setVisibility(View.GONE);
        } else if (tag == 1) {
            baseViewHolder.setText(R.id.tv_status_ipm, s.op_nickname);
            baseViewHolder.setVisible(R.id.tv_status_ipm, true);
            baseViewHolder.getView(R.id.tv_time).setVisibility(View.GONE);
        } else if (tag == 2 || tag == 3) {
            baseViewHolder.setText(R.id.tv_status_ipm, s.op_nickname);
            baseViewHolder.setVisible(R.id.tv_status_ipm, true);
            baseViewHolder.getView(R.id.tv_time).setVisibility(View.VISIBLE);
            baseViewHolder.setText(R.id.tv_time, s.limit_deadline);
        }
        baseViewHolder.setText(R.id.tv_cancel_btn, tag == 2 ? "解除" : "取消");
        ImageView img = baseViewHolder.getView(R.id.img_ipm);
        GlideUtils.loadTopRoundImage(baseViewHolder.itemView.getContext(), img, s.icon);
        baseViewHolder.setText(R.id.tv_ipm_name, s.nickname);
        CommonUtil.setSexAndAge(getContext(), s.sex, s.age, baseViewHolder.getView(R.id.iv_sex));
        baseViewHolder.getView(R.id.tv_cancel_btn).setOnClickListener(v -> {
            iClickCancelData.clickCancel(s);
        });
    }

    /**
     * 0房管，1 拉黑，2踢出，3禁言
     */
    public void updateTag(int position, PartyManagerListBean list) {
        tag = position;
        getData().clear();
        if (tag == 0) {
            getData().addAll(list.room_manager.list);
        } else if (tag == 1) {
            getData().addAll(list.black_list.list);
        } else if (tag == 2) {
            getData().addAll(list.kick_list.list);
        } else if (tag == 3) {
            getData().addAll(list.mute_list.list);
        }
        notifyDataSetChanged();
    }

    public interface IClickCancelData {
        void clickCancel(PartyManagerListBean.ListDTO listDTO);
    }
}
