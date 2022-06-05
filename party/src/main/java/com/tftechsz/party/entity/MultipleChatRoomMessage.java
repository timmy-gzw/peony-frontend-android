package com.tftechsz.party.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

/**
 * 包 名 : com.tftechsz.party.entity
 * 描 述 : TODO
 */
public class MultipleChatRoomMessage implements MultiItemEntity {
    private final int itemType;
    private final ChatRoomMessage message;

    public ChatRoomMessage getMessage() {
        return message;
    }

    public MultipleChatRoomMessage(int itemType, ChatRoomMessage message) {
        this.itemType = itemType;
        this.message = message;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
