package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity.req
 * 描 述 : TODO
 */
public class UpdateAccostSettingBean {
    public int id;
    public String type;
    public AccostSettingListBean data;

    public UpdateAccostSettingBean(String type, AccostSettingListBean data) {
        this.type = type;
        this.data = data;
    }

    public UpdateAccostSettingBean(int id, String type, AccostSettingListBean data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }
}
