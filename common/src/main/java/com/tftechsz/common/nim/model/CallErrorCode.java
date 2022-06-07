package com.tftechsz.common.nim.model;

/**
 * 组件内部错误码
 */
public interface CallErrorCode {
    /**
     * 组件内部状态错误
     */
    int STATUS_ERROR = 2000;

    /**
     * 其他端已经接受
     */
    int OTHER_CLIENT_ACCEPT = 2001;

    /**
     * 其他端拒绝
     */
    int OTHER_CLIENT_REJECT = 2002;

    /**
     * rtc 断开连接
     */
    int NERTC_DISCONNECT = 2011;

    /**
     * 请求token失败
     */
    int LOAD_TOKEN_ERROR = 2021;
}
