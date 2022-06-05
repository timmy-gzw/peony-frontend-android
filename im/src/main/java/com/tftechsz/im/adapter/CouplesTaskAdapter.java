package com.tftechsz.im.adapter;


import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.GroupCoupleDto;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 情侣任务
 */
public class CouplesTaskAdapter extends BaseQuickAdapter<GroupCoupleDto.CoupleTask, BaseViewHolder> {

    private ICallBackGet iCallBackGet;

    public CouplesTaskAdapter(ICallBackGet iCallBackGet) {
        super(R.layout.adapter_couples_task);
        this.iCallBackGet = iCallBackGet;

    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, GroupCoupleDto.CoupleTask item, @NotNull List<?> payloads) {
        setData(holder, item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GroupCoupleDto.CoupleTask item) {
        setData(helper, item);
    }

    public void updateDate(GroupCoupleDto.CoupleTask coupleTask, int position) {
        setData(position, coupleTask);
        notifyItemChanged(position);
    }

    public void setData(BaseViewHolder helper, GroupCoupleDto.CoupleTask item) {
        ConstraintLayout constraintLayout = helper.getView(R.id.con_root_gg);
        TextView tvName = helper.getView(R.id.tv_couples_task_name);
        TextView tvHint = helper.getView(R.id.tv_hint);
        ImageView imgBtn = helper.getView(R.id.img_click_btn);
        ImageView imgHeader = helper.getView(R.id.img_header);
        GlideUtils.loadImage(getContext(), imgHeader, item.icon);
        tvName.setText(Html.fromHtml(item.title));
        switch (item.status) {// 0/1/2 进行中、领取、已完成
            case 0:
                imgBtn.setImageResource(R.mipmap.chat_couples_task_btn_loading);
                tvName.setAlpha(1F);
                tvHint.setAlpha(1F);
                constraintLayout.setBackground(helper.itemView.getContext().getDrawable(R.drawable.bg_couples_task_loading));
                break;
            case 1:
                imgBtn.setImageResource(R.mipmap.chat_couples_task_btn_get);
                tvName.setAlpha(1F);
                tvHint.setAlpha(1F);
                constraintLayout.setBackground(helper.itemView.getContext().getDrawable(R.drawable.bg_couples_task_get));
                break;
            case 2:
                imgBtn.setImageResource(R.mipmap.chat_couples_task_btn_complate);
                tvName.setAlpha(0.6F);
                tvHint.setAlpha(0.6F);
                constraintLayout.setBackground(helper.itemView.getContext().getDrawable(R.drawable.bg_couples_task_com));
                break;
        }
        tvHint.setText(Html.fromHtml(item.tips));
        helper.getView(R.id.img_click_btn).setOnClickListener(v -> iCallBackGet.get(getItemPosition(item), item));
    }

    public interface ICallBackGet {
        void get(int position, GroupCoupleDto.CoupleTask coupleTask);
    }
}
