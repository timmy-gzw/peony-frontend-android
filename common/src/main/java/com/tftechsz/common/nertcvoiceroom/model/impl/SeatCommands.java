package com.tftechsz.common.nertcvoiceroom.model.impl;

import android.text.TextUtils;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nertcvoiceroom.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class SeatCommands {
    public static CustomNotification applySeat(VoiceRoomInfo voiceRoomInfo, VoiceRoomUser user, VoiceRoomSeat seat) {
        String content;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SeatCommandDef.COMMAND, SeatCommandDef.APPLY_SEAT);
            jsonObject.put(SeatCommandDef.INDEX, seat.getIndex());
            jsonObject.put(SeatCommandDef.NICK, user.nickname);
            jsonObject.put(SeatCommandDef.AVATAR, user.icon);
            content = jsonObject.toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }

        CustomNotification notification = new CustomNotification();
        notification.setSessionId(voiceRoomInfo.getTid());
        notification.setSessionType(SessionTypeEnum.Team);
        notification.setFromAccount(user.account);
        notification.setContent(content);
        return notification;
    }

    public static CustomNotification leaveSeat(VoiceRoomInfo voiceRoomInfo, VoiceRoomUser user, VoiceRoomSeat seat) {
        String content;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SeatCommandDef.COMMAND, SeatCommandDef.LEAVE_SEAT);
            jsonObject.put(SeatCommandDef.INDEX, seat.getIndex());
            content = jsonObject.toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(voiceRoomInfo.getTid());
        notification.setSessionType(SessionTypeEnum.P2P);
        notification.setFromAccount(user.account);
        notification.setContent(content);
        return notification;
    }

    public static CustomNotification onSeat(VoiceRoomInfo voiceRoomInfo, VoiceRoomUser user, VoiceRoomSeat seat) {
        String content;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SeatCommandDef.COMMAND, SeatCommandDef.ON_SEAT);
            jsonObject.put(SeatCommandDef.INDEX, seat.getIndex());
            jsonObject.put(SeatCommandDef.NICK, seat.getNickname());
            jsonObject.put(SeatCommandDef.AVATAR, seat.getIcon());
            content = jsonObject.toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        CustomNotification notification = new CustomNotification();
        if (voiceRoomInfo != null)
            notification.setSessionId(voiceRoomInfo.getCreatorAccount());
        notification.setSessionType(SessionTypeEnum.P2P);
        notification.setFromAccount(user.account);
        notification.setContent(content);
        return notification;
    }

    public static CustomNotification cancelSeatApply(VoiceRoomInfo voiceRoomInfo, VoiceRoomUser user, VoiceRoomSeat seat) {
        String content;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SeatCommandDef.COMMAND, SeatCommandDef.CANCEL_SEAT_APPLY);
            jsonObject.put(SeatCommandDef.INDEX, seat.getIndex());
            content = jsonObject.toString();
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(voiceRoomInfo.getTid());
        notification.setSessionType(SessionTypeEnum.Team);
        notification.setFromAccount(user.account);
        notification.setContent(content);
        return notification;
    }

    public static int commandFrom(CustomNotification notification) {
        String content = notification.getContent();
        if (TextUtils.isEmpty(content)) {
            return 0;
        }
        JSONObject jsonObject = JsonUtil.parse(content);
        if (jsonObject == null) {
            return 0;
        }
        return jsonObject.optInt(SeatCommandDef.COMMAND, 0);
    }

    public static VoiceRoomSeat seatFrom(CustomNotification notification) {
        String content = notification.getContent();
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        JSONObject jsonObject = JsonUtil.parse(content);
        if (jsonObject == null) {
            return null;
        }
        int command = jsonObject.optInt(SeatCommandDef.COMMAND, 0);
        int index = jsonObject.optInt(SeatCommandDef.INDEX, -1);
        if (index < 0) {
            return null;
        }
        switch (command) {
            case SeatCommandDef.APPLY_SEAT: {
                String nick = jsonObject.optString(SeatCommandDef.NICK);
                String avatar = jsonObject.optString(SeatCommandDef.AVATAR);
                VoiceRoomUser user = new VoiceRoomUser(notification.getFromAccount(), nick, avatar);
                return new VoiceRoomSeat(index, VoiceRoomSeat.Status.APPLY, VoiceRoomSeat.Reason.INIT, user);
            }
            case SeatCommandDef.CANCEL_SEAT_APPLY:
            case SeatCommandDef.LEAVE_SEAT: {
                return new VoiceRoomSeat(index, VoiceRoomSeat.Status.INIT, VoiceRoomSeat.Reason.INIT, null);
            }
            case SeatCommandDef.ON_SEAT: {
                String nick = jsonObject.optString(SeatCommandDef.NICK);
                String avatar = jsonObject.optString(SeatCommandDef.AVATAR);
                return new VoiceRoomSeat(index, VoiceRoomSeat.Status.ON, VoiceRoomSeat.Status.ON, nick, avatar);
            }
        }
        return null;
    }
}
