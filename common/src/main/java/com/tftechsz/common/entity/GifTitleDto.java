package com.tftechsz.common.entity;

/**
 * 包 名 : com.tftechsz.common.entity
 * 描 述 : TODO
 */
public class GifTitleDto {
    private int child_cate; // 1人气 2豪华 3新奇 4贵族 5背包
    private String name;
    private boolean is_active;

    public int getChild_cate() {
        return child_cate;
    }

    public void setChild_cate(int child_cate) {
        this.child_cate = child_cate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}
