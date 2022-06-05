package com.tftechsz.im.adapter;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.AirdropDetailMemberDto;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

public class AirdropDetailAdapter extends BaseQuickAdapter<AirdropDetailMemberDto, BaseViewHolder> {
    private final UserProviderService service;

    public AirdropDetailAdapter() {
        super(R.layout.item_airdrop_detail);
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AirdropDetailMemberDto item) {
        GlideUtils.loadRouteImage(getContext(), baseViewHolder.getView(R.id.iv_avatar), item.icon);
        GlideUtils.loadRouteImage(getContext(), baseViewHolder.getView(R.id.iv_gift), item.gift_image);
        baseViewHolder.setText(R.id.tv_name, item.nickname)
                .setText(R.id.tv_num, "x" + item.gift_num)
                .setText(R.id.tv_time, item.created_at);
        baseViewHolder.setVisible(R.id.tv_type_name, service.getUserId() == item.user_id);
        baseViewHolder.setVisible(R.id.iv_optimum, item.is_optimum == 1);


    }
}
