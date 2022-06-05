package com.tftechsz.common.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.PaymentTypeDto;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.GlideUtils;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class NoblePayAdapter extends BaseQuickAdapter<PaymentTypeDto, BaseViewHolder> {
    private boolean showRightArrow;

    public NoblePayAdapter() {
        super(R.layout.item_noble_pay);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PaymentTypeDto bean) {
        View root = helper.getView(R.id.root);
        ImageView ivRight = helper.getView(R.id.iv_right);
        ImageView icon = helper.getView(R.id.icon);
        TextView title = helper.getView(R.id.title);
        GlideUtils.loadRouteImage(getContext(), icon, bean.image_white);
        title.setText(bean.title);
        if (isWeChat(helper.getLayoutPosition())) {
            root.setBackgroundResource(R.drawable.shape_noble_wechat);
        } else if (isAliPay(helper.getLayoutPosition())) {
            root.setBackgroundResource(R.drawable.shape_noble_alipay);
        }
        ivRight.setVisibility(showRightArrow ? View.VISIBLE : View.GONE);
    }

    public boolean isWeChat(int pos) {
        return getItemCount() > pos && TextUtils.equals(getItem(pos).type, Interfaces.WECHAT);
    }

    public boolean isAliPay(int pos) {
        return getItemCount() > pos && TextUtils.equals(getItem(pos).type, Interfaces.ALIPAY);
    }

    public void showRightArrow(boolean b) {
        showRightArrow = b;
    }
}
