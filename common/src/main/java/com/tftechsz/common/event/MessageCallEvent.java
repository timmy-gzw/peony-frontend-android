package com.tftechsz.common.event;


import com.netease.nimlib.sdk.msg.model.IMMessage;


public class MessageCallEvent {
    public IMMessage message;
    public int type;

    public MessageCallEvent(IMMessage message) {
        this.message = message;
    }



    public MessageCallEvent(IMMessage message,int type) {
        this.message = message;
        this.type = type;
    }
}
