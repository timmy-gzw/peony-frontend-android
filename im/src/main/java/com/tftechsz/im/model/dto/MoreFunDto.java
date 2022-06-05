package com.tftechsz.im.model.dto;

public class MoreFunDto {

    public int bg;
    public String content;
    public int id;   //0 空投  1： 骰子 2 猜拳 3：组情侣 4 招募红包 5：语聊
    public boolean show_new;

    public MoreFunDto(int id, String content, int bg) {
        this.bg = bg;
        this.id = id;
        this.content = content;
    }

    public MoreFunDto(int id, String content, int bg, boolean show_new) {
        this.bg = bg;
        this.content = content;
        this.id = id;
        this.show_new = show_new;
    }
}
