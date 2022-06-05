package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

/**
 * 家族
 */
public interface FamilyService extends IProvider {
    /**
     * 查询家族是否存在
     */
    void searchFamily(String tid,ResponseObserver<BaseResponse<FamilyIdDto>> responseObserver);

    /**
     * 查询家族是否存在
     */
    void familySign(String fromType,ResponseObserver<BaseResponse<Boolean>> responseObserver);
}
