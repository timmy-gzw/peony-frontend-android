package com.tftechsz.common.event;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

import java.util.List;

public class ChatEvent {
    public List<ChatRoomMessage> messages;

    public ChatEvent(List<ChatRoomMessage> messages) {
        this.messages = messages;
    }
}
