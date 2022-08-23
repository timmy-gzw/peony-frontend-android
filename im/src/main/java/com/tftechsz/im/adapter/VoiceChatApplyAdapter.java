package com.tftechsz.im.adapter;

import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.RoomApplyDto;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

import androidx.constraintlayout.widget.ConstraintLayout;

public class VoiceChatApplyAdapter extends BaseQuickAdapter<RoomApplyDto, BaseViewHolder> {

    private int mUserId;

    public VoiceChatApplyAdapter(int userId) {
        super(R.layout.item_voice_chat_apply);
        mUserId = userId;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, RoomApplyDto item) {
        GlideUtils.loadRoundImageRadius(getContext(), helper.getView(R.id.iv_avatar), item.icon);
        ConstraintLayout root = helper.getView(R.id.root);
        ImageView nobleLabel = helper.getView(R.id.noble_label);
        helper.setText(R.id.tv_name, item.nickname);
        if (helper.getLayoutPosition() == 0) {
            root.setPadding(ConvertUtils.dp2px(20), ConvertUtils.dp2px(20), ConvertUtils.dp2px(20), ConvertUtils.dp2px(8));
        } else if (helper.getLayoutPosition() + 1 == getItemCount()) {
            root.setPadding(ConvertUtils.dp2px(20), ConvertUtils.dp2px(8), ConvertUtils.dp2px(20), ConvertUtils.dp2px(20));
        } else {
            root.setPadding(ConvertUtils.dp2px(20), ConvertUtils.dp2px(8), ConvertUtils.dp2px(20), ConvertUtils.dp2px(8));
        }
        com.netease.nim.uikit.common.CommonUtil.setBadge(nobleLabel, item.getBadge());
        CommonUtil.setSexAndAge(getContext(), item.sex, item.age, helper.getView(R.id.tv_sex));
        if (mUserId == item.user_id) {  //自己
            helper.setGone(R.id.tv_agree, true);
            helper.setText(R.id.tv_reject, "取消申请");
        } else {
            if (item.confirm == 0) {   //贵族优先上麦
                helper.setGone(R.id.tv_agree, true);
                helper.setGone(R.id.tv_reject, true);
                helper.setVisible(R.id.tv_first, true);
            } else {
                helper.setVisible(R.id.tv_agree, true);
                helper.setVisible(R.id.tv_reject, true);
                helper.setGone(R.id.tv_first, true);
                helper.setText(R.id.tv_reject, "拒绝");
            }
        }
    }
}
