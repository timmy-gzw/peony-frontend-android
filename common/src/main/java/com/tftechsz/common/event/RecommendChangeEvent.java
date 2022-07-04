package com.tftechsz.common.event;

/**
 * 个性化推荐 开关更改通知
 */
public class RecommendChangeEvent {

    private boolean isOpen;

    public RecommendChangeEvent(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
