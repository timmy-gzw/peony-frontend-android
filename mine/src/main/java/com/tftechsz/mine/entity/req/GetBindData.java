package com.tftechsz.mine.entity.req;

import java.io.Serializable;

/**
 * 包 名 : com.tftechsz.mine.entity.req
 * 描 述 : TODO
 */
public class GetBindData implements Serializable {

    public String message;
    public Data phone;
    public Data wecaht;

    public static class Data implements Serializable {
        public int is_bind;
        public int wait_day;
        public String value;
        public int is_repair;
    }
}
