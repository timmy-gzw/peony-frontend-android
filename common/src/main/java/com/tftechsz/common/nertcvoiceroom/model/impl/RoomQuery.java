package com.tftechsz.common.nertcvoiceroom.model.impl;

import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.MemberOption;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nertcvoiceroom.util.ConvertCallback;

import java.util.ArrayList;
import java.util.List;

public final class RoomQuery {
    private static final int MUTE_DURATION = 30/*day*/
            * 24/*hour*/ * 60/*minute*/ * 60/*second*/;

    private final String roomId;

    private final ChatRoomService chatRoomService;

    public RoomQuery(VoiceRoomInfo voiceRoomInfo, ChatRoomService service) {
        this.roomId = voiceRoomInfo.getRoom_id();
        this.chatRoomService = service;
    }

    public void fetchRoomMute(final RequestCallback<Boolean> callback) {
        fetchRoomInfo(new ConvertCallback.Converter<ChatRoomInfo, Boolean>() {
            @Override
            public Boolean convert(ChatRoomInfo param) {
                return param != null && param.isMute();
            }
        }, callback);
    }

    public void fetchMember(String account, final RequestCallback<ChatRoomMember> callback) {
        List<String> accounts = new ArrayList<>();
        accounts.add(account);
        fetchRoomMembers(accounts, new ConvertCallback.Converter<List<ChatRoomMember>, ChatRoomMember>() {
            @Override
            public ChatRoomMember convert(List<ChatRoomMember> param) {
                return param != null && !param.isEmpty() ? param.get(0) : null;
            }
        }, callback);
    }

    public void isMember(final String account, final RequestCallback<Boolean> callback) {
        fetchRoomMembers(new ConvertCallback.Converter<List<ChatRoomMember>, Boolean>() {
            @Override
            public Boolean convert(List<ChatRoomMember> param) {
                return containsAccount(param, account);
            }
        }, callback);
    }

    public void fetchMembersByAccount(final List<String> excludeAccounts, final boolean include, final RequestCallback<List<VoiceRoomUser>> callback) {
        fetchRoomMembers(new ConvertCallback.Converter<List<ChatRoomMember>, List<VoiceRoomUser>>() {
            @Override
            public List<VoiceRoomUser> convert(List<ChatRoomMember> param) {
                return byAccount(param, include, excludeAccounts);
            }
        }, callback);
    }

    public void fetchMembersByMuted(final boolean include, final RequestCallback<List<VoiceRoomUser>> callback) {
        fetchRoomMembers(new ConvertCallback.Converter<List<ChatRoomMember>, List<VoiceRoomUser>>() {
            @Override
            public List<VoiceRoomUser> convert(List<ChatRoomMember> param) {
                return byMuted(param, include);
            }
        }, callback);
    }

    public void muteMember(VoiceRoomUser member, boolean mute, RequestCallback<Void> callback) {
        MemberOption option = new MemberOption(roomId, member.getAccount());
        chatRoomService.markChatRoomTempMute(true, mute ? MUTE_DURATION : 0, option).setCallback(callback);
    }

    private <T> void fetchRoomInfo(ConvertCallback.Converter<ChatRoomInfo, T> converter, RequestCallback<T> callback) {
        chatRoomService.fetchRoomInfo(roomId)
                .setCallback(new ConvertCallback<>(callback, converter));
    }

    private <T> void fetchRoomMembers(ConvertCallback.Converter<List<ChatRoomMember>, T> converter, RequestCallback<T> callback) {
        chatRoomService.fetchRoomMembers(roomId, MemberQueryType.GUEST, 0, 10000)
                .setCallback(new ConvertCallback<>(callback, converter));
    }

    private <T> void fetchRoomMembers(List<String> accounts, ConvertCallback.Converter<List<ChatRoomMember>, T> converter, RequestCallback<T> callback) {
        chatRoomService.fetchRoomMembersByIds(roomId, accounts)
                .setCallback(new ConvertCallback<>(callback, converter));
    }

    private static List<VoiceRoomUser> byMuted(List<ChatRoomMember> chatRoomMembers, boolean include) {
        List<VoiceRoomUser> result = new ArrayList<>();
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            boolean muted = chatRoomMember.isTempMuted() || chatRoomMember.isMuted();
            if (muted == include) {
                result.add(new VoiceRoomUser(chatRoomMember));
            }
        }
        return result;
    }

    private static List<VoiceRoomUser> byAccount(List<ChatRoomMember> chatRoomMembers, boolean include, List<String> accounts) {
        List<VoiceRoomUser> result = new ArrayList<>();
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            boolean contains = accounts.contains(chatRoomMember.getAccount());
            if (contains == include) {
                result.add(new VoiceRoomUser(chatRoomMember));
            }
        }
        return result;
    }

    private static boolean containsAccount(List<ChatRoomMember> chatRoomMembers, String account) {
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            if (chatRoomMember.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }
}
