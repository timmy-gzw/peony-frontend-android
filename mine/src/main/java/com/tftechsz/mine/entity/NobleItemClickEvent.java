package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class NobleItemClickEvent {
    public int position;
    public boolean btnClick;

    public NobleItemClickEvent(int position) {
        this.position = position;
    }

    public NobleItemClickEvent(int position, boolean btnClick) {
        this.position = position;
        this.btnClick = btnClick;
    }
}
