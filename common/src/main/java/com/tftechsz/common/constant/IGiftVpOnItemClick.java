package com.tftechsz.common.constant;

import com.tftechsz.common.entity.GiftDto;

/**
 * 包 名 : com.tftechsz.common.constant
 * 描 述 : TODO
 */
public interface IGiftVpOnItemClick {
    void onitemClickGiftInfoActivityGet(GiftDto giftDto);

    void onItemCurrent(int pos);
}
