package com.tftechsz.family.adapter;

import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.family.R;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 聊天室成员
 */
public class RoomMemberAdapter extends BaseQuickAdapter<FamilyMemberDto, BaseViewHolder> {
    UserProviderService service;

    public RoomMemberAdapter(@Nullable List<FamilyMemberDto> data) {
        super(R.layout.item_room_member, data);
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, FamilyMemberDto item) {
        ImageView ivAvatar = helper.getView(R.id.iv_avatar);
        GlideUtils.loadRoundImageRadius(getContext(), ivAvatar, item.icon);
        helper.setText(R.id.tv_name, item.nickname);
        CommonUtil.setSexAndAge(getContext(), item.sex, item.age, helper.getView(R.id.tv_sex));
        helper.setVisible(R.id.tv_type_name, service.getUserId() == item.user_id);
    }


}
