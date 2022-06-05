package com.tftechsz.common.entity;

/**
 * 包 名 : com.tftechsz.party.entity
 * 描 述 : TODO
 */
public class CallbackExt {

    public int errCode;
    public CallbackExtDTO callbackExt;
    public int responseCode;

    public static class CallbackExtDTO {
        public int code;
        public String body;
    }
}
