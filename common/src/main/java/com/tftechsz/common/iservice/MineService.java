package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.PaymentDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

/**
 * 个人真人
 */
public interface MineService extends IProvider {
    /**
     * 获取真人是否认证
     */
    void getRealInfo(ResponseObserver<BaseResponse<RealStatusInfoDto>> responseObserver);

    /**
     * 获取是否实名
     */
    void getSelfInfo(ResponseObserver<BaseResponse<RealStatusInfoDto>> responseObserver);

    /**
     * 获取用户信息
     */
    void getUserInfo(ResponseObserver<BaseResponse<UserInfo>> responseObserver);

    /**
     * 检查女性用户可以私信次数
     *
     * @param
     */
    void getMsgCheck(String user_id, ResponseObserver<BaseResponse<MsgCheckDto>> responseObserver);

    /**
     * 打电话前是否有真人认证
     *
     * @param
     */
    void getCallCheck(String user_id, int from, ResponseObserver<BaseResponse<CallCheckDto>> responseObserver);

    /**
     * 获取充值金额
     *
     * @param
     */
    void getRechargeNewList(ResponseObserver<BaseResponse<PaymentDto>> responseObserver);

    /**
     * 用户事件
     *
     * @param scene
     * @param event
     * @param responseObserver
     */
    void trackEvent(String scene, String event, String index, String extend, ResponseObserver<BaseResponse<Boolean>> responseObserver);

    void getUserInfoById(String user_id, ResponseObserver<BaseResponse<UserInfo>> responseObserver);
}
