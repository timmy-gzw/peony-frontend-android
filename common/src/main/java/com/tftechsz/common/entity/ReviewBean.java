package com.tftechsz.common.entity;

public class ReviewBean {
    private boolean r;//是否正在审核
    private boolean srl;//是否获取定位权限 false：不获取  true：获取权限

    public boolean isR() {
        return r;
    }

    public boolean isSrl() {
        return srl;
    }
}
