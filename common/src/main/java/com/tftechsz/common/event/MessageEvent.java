package com.tftechsz.common.event;

import com.tftechsz.common.entity.MessageInfo;

import java.util.List;

public class MessageEvent {
    public List<MessageInfo> messageInfoList;


    public MessageEvent(List<MessageInfo> messageInfoList){
        this.messageInfoList = messageInfoList;
    }
}
