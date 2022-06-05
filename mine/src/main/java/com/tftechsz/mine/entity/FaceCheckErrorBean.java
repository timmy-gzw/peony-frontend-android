package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class FaceCheckErrorBean {
    public String token;
    public int code;
    public String msg;

    public FaceCheckErrorBean(String token, int code, String msg) {
        this.token = token;
        this.code = code;
        this.msg = msg;
    }
}
