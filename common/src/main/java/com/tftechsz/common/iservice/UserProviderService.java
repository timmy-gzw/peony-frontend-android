package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.entity.IntegralDto;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

public interface UserProviderService extends IProvider {

    void setToken(String token);

    String getToken();

    void setUserId(int id);

    UserInfo getUserInfo();

    ConfigInfo getConfigInfo();

    int getUserId();

    void setUserInfo(UserInfo userInfo);

    void setConfigInfo(String configInfo);

    void setRoomToken(String token);

    void setChannelName(String channelName);

    void setCallId(String callId);

    void setCallIsMatch(boolean isMatch);

    boolean getCallIsMatch();

    String getCallId();

    String getRoomToken();

    String getChannelName();

    String getMatchType();

    /**
     * 拨打类型 1：语音  2：视频
     */
    void setCallType(int type);

    //type.force = 强制匹配，type.inquiry = 询问匹配
    void setMatchType(String type);

    int getCallType();

    void setChannelId(long channelId);

    long getChannelId();

    /**
     * 获取基本信息， 如金币 积分等
     */
    void getField(String type, String field, ResponseObserver<BaseResponse<IntegralDto>> responseObserver);

}
