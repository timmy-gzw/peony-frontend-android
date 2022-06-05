package com.tftechsz.common.nim.model;

import com.netease.nimlib.sdk.RequestCallback;
import com.tftechsz.common.entity.LiveTokenDto;

/**
 *获取token的服务
 */
public interface TokenService {
    void getToken(long uid, RequestCallback<LiveTokenDto> callback);
}
