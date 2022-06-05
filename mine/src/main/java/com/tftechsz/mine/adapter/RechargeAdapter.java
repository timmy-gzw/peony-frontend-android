package com.tftechsz.mine.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class RechargeAdapter extends BaseQuickAdapter<RechargeDto, BaseViewHolder> {
    public RechargeAdapter() {
        super(R.layout.item_charge_list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, RechargeDto item) {
        GlideUtils.loadRouteImage(BaseApplication.getInstance(), helper.getView(R.id.iv_avatar), item.image);
        helper.setText(R.id.tv_coin, item.coin)
                .setText(R.id.tv_desc, item.title);
        helper.setText(R.id.tv_rmb, "¥" + item.rmb);
        helper.setText(R.id.tv_send, item.desc);
        helper.setVisible(R.id.view, helper.getLayoutPosition() != getItemCount() - 1);
        helper.setGone(R.id.tv_send, TextUtils.isEmpty(item.desc));
        helper.setGone(R.id.tv_desc, TextUtils.isEmpty(item.title));
    }
}
