package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

/**
 * 关注
 */
public interface AttentionService extends IProvider {
    /**
     * 获取是否关注了用户
     */
    void getIsAttention(int uid, ResponseObserver<BaseResponse<Boolean>> responseObserver);

    /**
     * 关注用户
     */
    void attentionUser(int uid, ResponseObserver<BaseResponse<Boolean>> responseObserver);

    /**
     * 拉黑用户
     */
    void blackUser(int uid, ResponseObserver<BaseResponse<Boolean>> responseObserver);

    void finishPartyActivity();
}
