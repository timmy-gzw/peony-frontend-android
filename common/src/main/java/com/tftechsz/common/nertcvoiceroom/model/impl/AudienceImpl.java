package com.tftechsz.common.nertcvoiceroom.model.impl;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.tftechsz.common.nertcvoiceroom.model.Audience;
import com.tftechsz.common.nertcvoiceroom.model.AudiencePlay;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nertcvoiceroom.model.ktv.MusicSing;
import com.tftechsz.common.nertcvoiceroom.util.DoneCallback;
import com.tftechsz.common.nertcvoiceroom.util.RequestCallbackEx;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;

import java.util.List;

public class AudienceImpl implements Audience {
    private final NERtcVoiceRoomInner voiceRoom;

    /**
     * 消息服务
     */
    private final MsgService msgService;

    /**
     * 房间信息
     */
    private VoiceRoomInfo voiceRoomInfo;

    /**
     * 资料信息
     */
    private VoiceRoomUser user;

    /**
     * 当前麦位
     */
    private VoiceRoomSeat mySeat;

    /**
     * 观众回调
     */
    private Callback callback;

    /**
     * cdn 模式下播放器控制
     */
    private final AudiencePlay audiencePlay = new AudiencePlayImpl();

    private final SeatStatusHelper statusRecorder;

    public AudienceImpl(NERTCVideoCallImpl voiceRoom) {
        this.voiceRoom = voiceRoom;
        this.statusRecorder = new SeatStatusHelper(voiceRoom);
        this.msgService = NIMClient.getService(MsgService.class);
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void applySeat(final VoiceRoomSeat seat, final RequestCallback<Void> callback) {
        if (mySeat != null && (mySeat.isOn() || mySeat.getStatus() == VoiceRoomSeat.Status.APPLY)) {
            if (callback != null) {
                callback.onFailed(-1);
            }
            return;
        }
        VoiceRoomSeat backup = seat.getBackup();
        backup.apply();
        backup.setUser(user);
        statusRecorder.updateSeat(backup, new SeatStatusHelper.ExecuteAction() {
            @Override
            public void onSuccess() {
                doApplySeat(seat, callback);
            }

            @Override
            public void onFail() {
                if (callback != null) {
                    callback.onFailed(-1);
                }
            }
        });

    }

    @Override
    public void onSeat(final VoiceRoomSeat seat, RequestCallback<Void> callback) {
        if (seat == null) {
            return;
        }
        seat.setReason(VoiceRoomSeat.Status.ON);
        onEnterSeat(seat, true);
//        sendNotification(SeatCommands.onSeat(voiceRoomInfo, user, seat), new RequestCallbackEx<Void>(callback) {
//            @Override
//            public void onSuccess(Void param) {
//                onEnterSeat(seat, true);
//                super.onSuccess(param);
//            }
//        });
    }


    private void doApplySeat(VoiceRoomSeat seat, final RequestCallback<Void> callback) {
        mySeat = seat;

        sendNotification(SeatCommands.applySeat(voiceRoomInfo, user, seat), new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                if (mySeat == null) {
                    return;
                }

                VoiceRoomSeat q = voiceRoom.getSeat(mySeat.getIndex());
                if (q.getStatus() == VoiceRoomSeat.Status.CLOSED) {
                    mySeat.setStatus(VoiceRoomSeat.Status.CLOSED);
                    voiceRoom.updateSeat(mySeat);
                    return;
                }
                mySeat.setStatus(VoiceRoomSeat.Status.APPLY);
//                mySeat.setUser(user);
//                voiceRoom.updateSeat(mySeat);

                if (callback != null) {
                    callback.onSuccess(param);
                }
            }

            @Override
            public void onFailed(int code) {
                if (callback != null) {
                    callback.onFailed(code);
                }
            }

            @Override
            public void onException(Throwable exception) {
                if (callback != null) {
                    callback.onException(exception);
                }
            }
        });
    }

    @Override
    public void cancelSeatApply(final RequestCallback<Void> callback) {
        if (mySeat == null) {
            return;
        }
        VoiceRoomSeat backup = mySeat.getBackup();
        backup.cancelApply();
        backup.setUser(user);
        statusRecorder.updateSeat(backup, new SeatStatusHelper.ExecuteAction() {
            @Override
            public void onSuccess() {
                final VoiceRoomSeat seat = mySeat;
                if (seat == null || seat.getStatus() == VoiceRoomSeat.Status.CLOSED) {
                    return;
                }
                seat.setReason(VoiceRoomSeat.Reason.CANCEL_APPLY);
                sendNotification(SeatCommands.cancelSeatApply(voiceRoomInfo, user, seat), new RequestCallbackEx<Void>(callback) {
                    @Override
                    public void onSuccess(Void param) {
                        if (mySeat != null && mySeat.getReason() == VoiceRoomSeat.Reason.CANCEL_APPLY) {
                            seat.cancelApply();
                            mySeat = null;
                        }

                        super.onSuccess(param);
                    }
                });
            }

            @Override
            public void onFail() {
                if (callback != null) {
                    callback.onFailed(-1);
                }
            }
        });

    }

    @Override
    public void leaveSeat(final RequestCallback<Void> callback) {
        if (mySeat == null) {
            return;
        }
        mySeat.setReason(VoiceRoomSeat.Reason.LEAVE);
        onLeaveSeat(mySeat, true);
        mySeat = null;
//        sendNotification(SeatCommands.leaveSeat(voiceRoomInfo, user, mySeat), new RequestCallbackEx<Void>(callback) {
//            @Override
//            public void onSuccess(Void param) {
//
//                super.onSuccess(param);
//            }
//        });
    }

    @Override
    public VoiceRoomSeat getSeat() {
        return mySeat != null ? voiceRoom.getSeat(mySeat.getIndex()) : null;
    }

    @Override
    public AudiencePlay getAudiencePlay() {
        return audiencePlay;
    }

    @Override
    public void refreshSeat() {
        voiceRoom.refreshSeats();
    }

    // wifi 2 4G  if enter room async delay, may be npe
    @Override
    public void restartAudioOrNot() {
        if (voiceRoomInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(voiceRoomInfo.rtmp_pull_url)) {
            return;
        }
        VoiceRoomSeat seat = getSeat();
        if (seat != null && seat.isOn()) {
            return;
        }
        getAudiencePlay().play(voiceRoomInfo.rtmp_pull_url);
    }

    public void enterRoom(VoiceRoomInfo voiceRoomInfo,
                          VoiceRoomUser user) {
        this.voiceRoomInfo = voiceRoomInfo;
        this.user = user;
        clearSeats();
    }

    public boolean leaveRoom(Runnable runnable) {
        if (!audiencePlay.isReleased()) {
            audiencePlay.release();
        }
        if (mySeat == null) {
            return false;
        }
        leaveSeat(new DoneCallback<Void>(runnable));
        return true;
    }

    void updateRoomInfo(ChatRoomInfo roomInfo) {
        if (roomInfo.isMute()) {
            muteText(true);
        }
    }

    public void updateMemberInfo(@NonNull ChatRoomMember member) {
        if (!member.isTempMuted() && !member.isMuted()) {
            muteText(false);
        }
    }

    public void initSeats(@NonNull List<VoiceRoomSeat> seats) {
        List<VoiceRoomSeat> userSeats = VoiceRoomSeat.find(seats, user.account);
        for (VoiceRoomSeat seat : userSeats) {
            if (seat != null && seat.isOn()) {
                mySeat = seat;
                onEnterSeat(seat, true);
                break;
            }
        }
    }

    public void clearSeats() {
        mySeat = null;
    }

    public void seatChange(VoiceRoomSeat seat) {
        // my seat is 'STATUS_CLOSE'
        if (seat.getStatus() == VoiceRoomSeat.Status.CLOSED
                && mySeat != null && mySeat.isSameIndex(seat)) {
            mySeat = null;
            if (callback != null) {
                callback.onSeatClosed();
            }
            return;
        }

        // others
        if (!seat.isSameAccount(user.account)) {
            // my seat is 'STATUS_NORMAL' by other
            if (seat.getStatus() == VoiceRoomSeat.Status.ON
                    && mySeat != null && mySeat.isSameIndex(seat)) {
                mySeat = null;
                if (callback != null) {
                    callback.onSeatApplyDenied(true);
                }
            }
        } else {
            switch (seat.getStatus()) {
                case VoiceRoomSeat.Status.ON:
                    mySeat = seat;
                    onEnterSeat(seat, false);
                    break;
                case VoiceRoomSeat.Status.AUDIO_MUTED:
                    mySeat = seat;
                    muteSeat();
                    break;
                case VoiceRoomSeat.Status.INIT:
                case VoiceRoomSeat.Status.FORBID:
                    if (seat.getReason() == VoiceRoomSeat.Reason.ANCHOR_DENY_APPLY) {
                        if (callback != null) {
                            callback.onSeatApplyDenied(false);
                        }
                    } else if (seat.getReason() == VoiceRoomSeat.Reason.ANCHOR_KICK) {
                        onLeaveSeat(seat, false);
                    }
                    mySeat = null;
                    break;
                case VoiceRoomSeat.Status.CLOSED:
                    if (mySeat != null && mySeat.getStatus() == VoiceRoomSeat.Status.APPLY) {
                        if (callback != null) {
                            callback.onSeatApplyDenied(false);
                        }
                    } else {
                        if (seat.getReason() == VoiceRoomSeat.Reason.ANCHOR_KICK) {
                            onLeaveSeat(seat, false);
                        }
                    }
                    mySeat = null;
                    break;
                case VoiceRoomSeat.Status.AUDIO_CLOSED:
                case VoiceRoomSeat.Status.AUDIO_CLOSED_AND_MUTED:
                    mySeat = seat;
                    break;
            }
        }
    }

    public void muteLocalAudio(boolean muted) {
        if (mySeat == null) {
            return;
        }
        mySeat.muteSelf(muted);
        voiceRoom.sendSeatUpdate(mySeat, null);
    }

    public void muteText(boolean mute) {
        if (callback != null) {
            callback.onTextMuted(mute);
        }
    }

    private void onEnterSeat(VoiceRoomSeat seat, boolean last) {
        mySeat = seat;
        if (voiceRoomInfo.isSupportCDN()) {
            voiceRoom.getPushTypeSwitcher().toRTC(voiceRoomInfo, Long.parseLong(user.account));
        }
        voiceRoom.startLocalAudio();
        if (voiceRoom.isLocalAudioMute()) {
            voiceRoom.muteLocalAudio(false);
        }
        if (callback != null) {
            callback.onEnterSeat(seat, last);
        }
    }

    private void onLeaveSeat(VoiceRoomSeat seat, boolean bySelf) {
        if (voiceRoomInfo.isSupportCDN() && voiceRoom.isInitial()) {
            voiceRoom.getPushTypeSwitcher().toCDN(voiceRoomInfo.http_pull_url);
        }

        MusicSing.shareInstance().leaveSet(user, true);
//        voiceRoom.enableEarback(false);
        voiceRoom.stopLocalAudio();
        if (callback != null) {
            callback.onLeaveSeat(seat, bySelf);
        }
    }

    private void muteSeat() {
        voiceRoom.stopLocalAudio();
        if (callback != null) {
            callback.onSeatMuted();
        }
    }

    private void sendNotification(CustomNotification notification, RequestCallback<Void> callback) {
        if (notification == null) {
            if (callback != null) {
                callback.onException(null);
            }
            return;
        }
        notification.setSendToOnlineUserOnly(false);
        msgService.sendCustomNotification(notification).setCallback(callback);
    }
}
