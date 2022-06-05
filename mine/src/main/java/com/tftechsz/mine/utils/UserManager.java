package com.tftechsz.mine.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.base.BaseApplication;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SPUtils;

public class UserManager {

    private final static String TOKEN_KEY = "token";
    private final static String USER_ID = "user_id";
    private final static String USER_INFO_KEY = "userInfo";
    private final static String CONFIG_INFO_KEY = "configInfo";
    private final static String ROOM_TOKEN = "room_token";
    private final static String CHANNEL_NAME = "channel_name";
    private final static String CALL_ID = "call_id";
    private final static String CALL_IS_MATCH = "call_is_match";
    private final static String CALL_TYPE = "call_type";
    private final static String CHANNEL_ID = "channel_id";
    private final static String MATCH_TYPE = "match_type";
    @SuppressLint("StaticFieldLeak")
    private static UserManager manager;
//    private final Context mContext;
    private String token;
    private int userId;
    private boolean isMatch;
    private String userInfo;
    private String configInfo;
    private String roomToken;   //语音视频聊天roomToken
    private String matchType;   //type.force = 强制匹配，type.inquiry = 询问匹配
    private String channelName;
    private String callId;
    private long channelId;  //房间号
    private int callType;   //1:语音 2 视频
    private final Gson gson;

    private UserManager(Context c) {
//        mContext = c;
        gson = new Gson();
    }

    public static void init(Context c) {
        if (null == manager)
            manager = new UserManager(c);

    }

    public static UserManager getInstance() throws RuntimeException {
        if (null == manager) {
            init(BaseApplication.getInstance());
        }
        return manager;

    }

    public void setToken(String token) {
        this.token = token;
        SPUtils.put(TOKEN_KEY, token);
    }

    public String getToken() throws RuntimeException {
        if (TextUtils.isEmpty(token)) {
            token = (String) SPUtils.get(TOKEN_KEY, "");
        }
        return token;
    }


    public void setCallId(String callId) {
        this.callId = callId;
        SPUtils.put(CALL_ID, callId);
    }

    public String getCallId() throws RuntimeException {
        if (TextUtils.isEmpty(callId)) {
            callId = (String) SPUtils.get(CALL_ID, "");
        }
        return callId;
    }


    public void setCallType(int callType) {
        this.callType = callType;
        SPUtils.put(CALL_TYPE, callType);
    }

    public int getCallType() throws RuntimeException {
        callType = SPUtils.getInt(CALL_TYPE, 0);
        return callType;
    }


    public void setChannelId(long channelId) {
        this.channelId = channelId;
        MMKVUtils.getInstance().encode(CHANNEL_ID,channelId);
    }

    public long getChannelId() throws RuntimeException {
        channelId = MMKVUtils.getInstance().decodeLong(CHANNEL_ID);
        return channelId;
    }


    public void setRoomToken(String roomToken) {
        this.roomToken = roomToken;
        MMKVUtils.getInstance().encode(ROOM_TOKEN,roomToken);
    }

    public String getRoomToken() throws RuntimeException {
        if (TextUtils.isEmpty(roomToken)) {
            roomToken = MMKVUtils.getInstance().decodeString(ROOM_TOKEN);
        }
        return roomToken;
    }


    public void setMatchType(String matchType) {
        this.matchType = matchType;
        SPUtils.put(MATCH_TYPE, matchType);
    }

    public String getMatchType() throws RuntimeException {
        if (TextUtils.isEmpty(matchType)) {
            matchType = (String) SPUtils.get(MATCH_TYPE, "");
        }
        return matchType;
    }


    public void setChannelName(String channelName) {
        this.channelName = channelName;
        MMKVUtils.getInstance().encode(CHANNEL_NAME,channelName);
    }

    public String getChannelName() throws RuntimeException {
        if (TextUtils.isEmpty(channelName)) {
            channelName = MMKVUtils.getInstance().decodeString(CHANNEL_NAME);
        }
        return channelName;
    }


    public void setUserId(int id) {
        this.userId = id;
        SPUtils.put(USER_ID, userId);
    }

    public int getUserId() throws RuntimeException {
        userId = SPUtils.getInt(USER_ID, 0);
        return userId;
    }


    public void setIsMatch(boolean isMatch) {
        this.isMatch = isMatch;
        SPUtils.put(CALL_IS_MATCH, isMatch);
    }

    public boolean isMatch() throws RuntimeException {
        isMatch = (boolean) SPUtils.get(CALL_IS_MATCH, false);
        return isMatch;
    }

    public UserInfo getUser() {
        UserInfo user = null;
        try {
            if (TextUtils.isEmpty(userInfo)) {
                userInfo = (String) SPUtils.get(USER_INFO_KEY, "");
            }
            user = gson.fromJson(this.userInfo, UserInfo.class);
            return user;
        } catch (Exception e) {
            SPUtils.remove(USER_INFO_KEY);
            e.printStackTrace();
        }
        return user;


    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = new Gson().toJson(userInfo);
        SPUtils.put(USER_INFO_KEY, this.userInfo);
    }


    public ConfigInfo getConfig() {
        ConfigInfo config = null;
        try {
            if (TextUtils.isEmpty(configInfo)) {
                configInfo = (String) SPUtils.get(CONFIG_INFO_KEY, "");
            }
            config = gson.fromJson(this.configInfo, ConfigInfo.class);
            return config;
        } catch (Exception e) {
            SPUtils.remove(CONFIG_INFO_KEY);
            e.printStackTrace();
        }
        return config;
    }

    public void setConfig(String configInfo) {
        this.configInfo = configInfo;
        SPUtils.put(CONFIG_INFO_KEY, this.configInfo);
    }


    public void clearUser() {
        this.userInfo = "";
        SPUtils.remove(USER_INFO_KEY);
    }

    public void clear() {
        SPUtils.clear();
    }


}
