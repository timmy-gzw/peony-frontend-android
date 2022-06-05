package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class BaseItemBean {
    public String left_icon;
    public String title;
    public int right_icon;
    public String right_txt;
    public String right_txt_color;

    public BaseItemBean(String left_icon, String title) {
        this(left_icon, title, 0);
    }

    public BaseItemBean(String left_icon, String title, int right_icon) {
        this.left_icon = left_icon;
        this.title = title;
        this.right_icon = right_icon;
    }

    public BaseItemBean(String left_icon, String title, String right_txt, String right_txt_color) {
        this.left_icon = left_icon;
        this.title = title;
        this.right_txt = right_txt;
        this.right_txt_color = right_txt_color;
    }
}
