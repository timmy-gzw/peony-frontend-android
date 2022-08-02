package com.tftechsz.common.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.netease.nim.uikit.common.PaymentTypeDto
import com.tftechsz.common.R
import com.tftechsz.common.constant.Interfaces
import com.tftechsz.common.utils.AppUtils
import com.tftechsz.common.utils.GlideUtils
import com.tftechsz.common.utils.Utils

class ChargePayTypeAdapter : BaseQuickAdapter<PaymentTypeDto, BaseViewHolder>(R.layout.item_recharge_pay_type_2) {

    override fun convert(holder: BaseViewHolder, item: PaymentTypeDto) {
        GlideUtils.loadRouteImage(context, holder.getView(R.id.iv_icon), item.image)
        holder.setText(R.id.tv_pay_title, item.title)
        val itemView = holder.itemView
        itemView.isSelected = item.is_active == 1
        holder.setGone(R.id.iv_checked,item.is_active != 1)
    }

    fun isWeChat(pos: Int): Boolean {
        return itemCount > pos && TextUtils.equals(getItem(pos).type, Interfaces.WECHAT)
    }

    fun isAliPay(pos: Int): Boolean {
        return itemCount > pos && TextUtils.equals(getItem(pos).type, Interfaces.ALIPAY)
    }

    fun isSXYWeChat(pos: Int): Boolean {
        if (!AppUtils.isWeChatAppInstalled(context)) {
            Utils.toast("手机未安装微信")
            return false
        }
        return itemCount > pos && TextUtils.equals(getItem(pos).type, Interfaces.SHOUXINYI_WECHAT)
    }

    fun isSXYAliPay(pos: Int): Boolean {
        if (!AppUtils.isAliPayInstalled(context)) {
            Utils.toast("手机未安装支付宝")
            return false
        }
        return itemCount > pos && TextUtils.equals(getItem(pos).type, Interfaces.SHOUXINYI_ALIPAY)
    }

    val dataPosition: Int
        get() {
            val size = data.size
            for (i in 0 until size) {
                if (data[i].is_active == 1) {
                    return i
                }
            }
            return 0
        }

    fun notifyDataPosition(position: Int) {
        val size = data.size
        for (i in 0 until size) {
            data[i].is_active = if (position == i) 1 else 0
        }
        notifyDataSetChanged()
    }
}