package com.tftechsz.mine.entity.dto;

/**
 * 包 名 : com.tftechsz.mine.entity.dto
 * 描 述 : TODO
 */
public class AccostCustomizeEvent {
    public int type; //0 add  1更新  2删除
    public int id;
    public String text;

    public AccostCustomizeEvent(int type) {
        this.type = type;
    }

    public AccostCustomizeEvent(int type, int id, String text) {
        this.type = type;
        this.id = id;
        this.text = text;
    }
}
