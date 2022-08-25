package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class VipPriceBean {
    public int id;
    public String title;
    public String aging;
    public String price;
    public String desc;
    public int is_active;
    public String tag;
    public String nav_title;
    public String origin_price_title;//原价
    public String reduce_price_title;//立省
    public boolean is_mid_line = true;


    public boolean isSel() {
        return is_active == 1;
    }

    public void setSel(boolean sel) {
        is_active = sel ? 1 : 0;
    }
}
