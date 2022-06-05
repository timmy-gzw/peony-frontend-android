package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

/**
 * 语音拨打权限
 */
public interface CallService extends IProvider {
    /**
     * 语音拨打权限
     */
    void callCheck(String userId,String type, ResponseObserver<BaseResponse<CallCheckDto>> responseObserver);

}
