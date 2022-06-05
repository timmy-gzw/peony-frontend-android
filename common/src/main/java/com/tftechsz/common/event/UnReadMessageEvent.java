package com.tftechsz.common.event;

/**
 * 未读消息数
 */
public class UnReadMessageEvent {
    public int num;
    public int p2pNumber;
    public UnReadMessageEvent(int num) {
        this.num = num;
    }

    public UnReadMessageEvent(int num,int p2pNumber) {
        this.num = num;
        this.p2pNumber = p2pNumber;
    }
}
