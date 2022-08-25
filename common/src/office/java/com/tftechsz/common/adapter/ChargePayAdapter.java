package com.tftechsz.common.adapter;

import android.text.TextUtils;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.PaymentTypeDto;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChargePayAdapter extends BaseQuickAdapter<PaymentTypeDto, BaseViewHolder> {

    public ChargePayAdapter(@Nullable List<PaymentTypeDto> data) {
        super(R.layout.item_recharge_pay_type, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, PaymentTypeDto item) {
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_icon), item.image);
        helper.setText(R.id.tv_pay_title, item.title);
        CheckBox checkBox = helper.getView(R.id.checkbox);
        checkBox.setChecked(item.is_active == 1);
    }

    public boolean isWeChat(int pos) {
        return getItemCount() > pos && TextUtils.equals(getItem(pos).type, Interfaces.WECHAT);
    }

    public boolean isAliPay(int pos) {
        return getItemCount() > pos && TextUtils.equals(getItem(pos).type, Interfaces.ALIPAY);
    }

    public boolean isSXYWeChat(int pos) {
        if (!AppUtils.isWeChatAppInstalled(getContext())) {
            Utils.toast("手机未安装微信");
            return false;
        }
        return getItemCount() > pos && TextUtils.equals(getItem(pos).type, Interfaces.SHOUXINYI_WECHAT);
    }

    public boolean isSXYAliPay(int pos) {
        if (!AppUtils.isAliPayInstalled(getContext())) {
            Utils.toast("手机未安装支付宝");
            return false;
        }
        return getItemCount() > pos && TextUtils.equals(getItem(pos).type, Interfaces.SHOUXINYI_ALIPAY);
    }


    public int getDataPosition() {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            if (getData().get(i).is_active == 1) {
                return i;
            }
        }
        return 0;
    }

    public void notifyDataPosition(int position) {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            getData().get(i).is_active = position == i ? 1 : 0;
        }
        notifyDataSetChanged();
    }
}
