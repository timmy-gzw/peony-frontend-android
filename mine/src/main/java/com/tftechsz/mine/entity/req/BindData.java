package com.tftechsz.mine.entity.req;

/**
 * 包 名 : com.tftechsz.mine.entity.req
 * 描 述 : TODO
 */
public class BindData {

    public String type; // wechat
    public DataDTO data;

    public static class DataDTO {
        public String phone;
        public String code;

        public DataDTO(String phone, String code) {
            this.phone = phone;
            this.code = code;
        }
    }

    public BindData(String type, DataDTO data) {
        this.type = type;
        this.data = data;
    }
}
