package com.tftechsz.mine.entity.dto;

import com.netease.nim.uikit.common.PaymentTypeDto;
import com.tftechsz.mine.entity.VipPriceBean;
import com.tftechsz.mine.entity.VipPrivilegeBean;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine
 * 描 述 : TODO
 */
public class VipConfigBean {
    public int picture_frame;  // 1, // 头像框素材ID
    public int chat_bubble;  // 3    // 聊天气泡素材ID

    public String shopping_title;
    public String privilege_title;
    public List<VipPriceBean> shopping;
    public List<VipPrivilegeBean> privilege;
    public List<PaymentTypeDto> payment_show;
}
