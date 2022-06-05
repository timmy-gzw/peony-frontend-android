package com.tftechsz.mine.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.utils.UserManager;


@Route(path = ARouterApi.USER_INFO_SERVICE, name = "获取用户信息服务")
public class UserProviderServiceImpl implements UserProviderService {

    @Override
    public void init(Context context) {
        UserManager.init(context);
    }

    @Override
    public void setToken(String token) {
        UserManager.getInstance().setToken(token);
    }

    @Override
    public String getToken() {
        return UserManager.getInstance().getToken();
    }

    @Override
    public void setUserId(int id) {
        UserManager.getInstance().setUserId(id);
    }

    @Override
    public UserInfo getUserInfo() {
        return UserManager.getInstance().getUser();
    }

    @Override
    public ConfigInfo getConfigInfo() {
        return UserManager.getInstance().getConfig();
    }

    @Override
    public int getUserId() {
        return UserManager.getInstance().getUserId();
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        UserManager.getInstance().setUserInfo(userInfo);
    }

    @Override
    public void setConfigInfo(String configInfo) {
        UserManager.getInstance().setConfig(configInfo);
    }

    @Override
    public void setRoomToken(String roomToken) {
        UserManager.getInstance().setRoomToken(roomToken);
    }

    @Override
    public void setChannelName(String channelName) {
        UserManager.getInstance().setChannelName(channelName);
    }

    @Override
    public void setCallId(String callId) {
        UserManager.getInstance().setCallId(callId);
    }

    @Override
    public void setCallIsMatch(boolean isMatch) {
        UserManager.getInstance().setIsMatch(isMatch);
    }

    @Override
    public boolean getCallIsMatch() {
        return UserManager.getInstance().isMatch();
    }

    @Override
    public String getCallId() {
        return UserManager.getInstance().getCallId();
    }

    @Override
    public String getRoomToken() {
        return UserManager.getInstance().getRoomToken();
    }

    @Override
    public String getChannelName() {
        return UserManager.getInstance().getChannelName();
    }

    @Override
    public String getMatchType() {
        return UserManager.getInstance().getMatchType();
    }

    @Override
    public void setCallType(int type) {
        UserManager.getInstance().setCallType(type);
    }

    @Override
    public void setMatchType(String type) {
        UserManager.getInstance().setMatchType(type);
    }

    @Override
    public int getCallType() {
        return UserManager.getInstance().getCallType();
    }

    @Override
    public void setChannelId(long channelId) {
        UserManager.getInstance().setChannelId(channelId);
    }

    @Override
    public long getChannelId() {
        return UserManager.getInstance().getChannelId();
    }

    @Override
    public void getField(String type, String field, ResponseObserver<BaseResponse<IntegralDto>> observer) {
        new RetrofitManager().createUserApi(MineApiService.class).getField(type, field)
                .compose(RxUtil.applySchedulers())
                .subscribe(observer);
    }

}
