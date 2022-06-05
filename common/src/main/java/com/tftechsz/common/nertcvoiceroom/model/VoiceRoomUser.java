package com.tftechsz.common.nertcvoiceroom.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 个人资料信息（语聊房使用）
 */
public class VoiceRoomUser implements Serializable {
    /**
     * 账号
     */
    public final String account;

    /**
     * 昵称
     */
    public final String nickname;

    /**
     * 头像
     */
    public final String icon;


    public VoiceRoomUser(String account, String nick, String avatar) {
        this.account = account;
        this.nickname = nick;
        this.icon = avatar;
    }

    public VoiceRoomUser(@NonNull ChatRoomMember chatRoomMember) {
        this.account = chatRoomMember.getAccount();
        this.nickname = chatRoomMember.getNick();
        this.icon = chatRoomMember.getAvatar();
    }

    private static final String ACCOUNT_KEY = "account";
    private static final String NICK_KEY = "nickname";
    private static final String AVATAR_KEY = "icon";

    public VoiceRoomUser(@NonNull JSONObject jsonObject) {
        account = jsonObject.optString(ACCOUNT_KEY, "");
        nickname = jsonObject.optString(NICK_KEY, "");
        icon = jsonObject.optString(AVATAR_KEY, "");
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(account)) {
                jsonObject.put(ACCOUNT_KEY, account);
            }
            if (!TextUtils.isEmpty(nickname)) {
                jsonObject.put(NICK_KEY, nickname);
            }
            if (!TextUtils.isEmpty(icon)) {
                jsonObject.put(AVATAR_KEY, icon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getAccount() {
        return account;
    }

    public String getNick() {
        return icon;
    }

    public String getAvatar() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoiceRoomUser other = (VoiceRoomUser) o;
        return account.equals(other.account);
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }
}
