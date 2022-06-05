package com.tftechsz.im.api;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.netease.nim.uikit.common.ChatMsg;

/**
 * 包 名 : com.tftechsz.im.api
 * 描 述 : TODO
 */
public class MultipleItem implements MultiItemEntity {
    private final int itemType;
    private final ChatMsg.JoinFamily person;

    public ChatMsg.JoinFamily getPerson() {
        return person;
    }


    public MultipleItem(int itemType, ChatMsg.JoinFamily person) {
        this.itemType = itemType;
        this.person = person;
    }

    @Override
    public int getItemType() {
        return itemType;
    }


}
