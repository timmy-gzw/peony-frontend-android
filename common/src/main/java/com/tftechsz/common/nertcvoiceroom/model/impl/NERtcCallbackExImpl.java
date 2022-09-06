package com.tftechsz.common.nertcvoiceroom.model.impl;

import android.graphics.Rect;

import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.lava.nertc.sdk.video.NERtcVideoStreamType;

public class NERtcCallbackExImpl implements NERtcCallbackEx {
    public NERtcCallbackExImpl() {

    }

    @Override
    public void onJoinChannel(int result, long channelId, long l1, long l2) {

    }

    @Override
    public void onLeaveChannel(int result) {}

    @Override
    public void onUserJoined(long uid) {}

    @Override
    public void onUserLeave(long uid, int reason) {}

    @Override
    public void onUserAudioStart(long uid) {}

    @Override
    public void onUserAudioStop(long uid) {}

    @Override
    public void onUserVideoStart(long uid,int maxProfile) {}

    @Override
    public void onUserVideoStop(long uid) {}

    @Override
    public void onDisconnect(int reason) {}

    @Override
    public void onClientRoleChange(int i, int i1) {

    }

    @Override
    public void onUserSubStreamVideoStart(long l, int i) {

    }

    @Override
    public void onUserSubStreamVideoStop(long l) {

    }

    @Override
    public void onUserAudioMute(long uid, boolean muted) {}

    @Override
    public void onUserVideoMute(long uid, boolean muted) {}

    @Override
    public void onFirstAudioDataReceived(long uid) {}

    @Override
    public void onFirstVideoDataReceived(long uid) {}

    @Override
    public void onFirstAudioFrameDecoded(long uid) {}

    @Override
    public void onFirstVideoFrameDecoded(long uid,int width, int height) {}

    @Override
    public void onUserVideoProfileUpdate(long uid, int maxProfile) {}

    @Override
    public void onAudioDeviceChanged(int selected) {}

    @Override
    public void onAudioDeviceStateChange(int deviceType, int deviceState) {}

    @Override
    public void onVideoDeviceStageChange(int deviceState) {}

    @Override
    public void onConnectionTypeChanged(int newConnectionType) {}

    @Override
    public void onReconnectingStart() {

    }

    @Override
    public void onReJoinChannel(int result, long channelId) {
    }

    @Override
    public void onAudioMixingStateChanged(int reason) {
    }

    @Override
    public void onAudioMixingTimestampUpdate(long timestampMs) {
    }

    @Override
    public void onAudioEffectTimestampUpdate(long l, long l1) {

    }

    @Override
    public void onAudioEffectFinished(int effectId) {
    }

    @Override
    public void onLocalAudioVolumeIndication(int volume) {
    }


    @Override
    public void onRemoteAudioVolumeIndication(NERtcAudioVolumeInfo[] volumeArray, int totalVolume) {
    }

    @Override
    public void onLiveStreamState(String taskId, String pushUrl, int liveState) {
    }

    @Override
    public void onConnectionStateChanged(int i, int i1) {

    }

    @Override
    public void onCameraFocusChanged(Rect rect) {

    }

    @Override
    public void onCameraExposureChanged(Rect rect) {

    }

    @Override
    public void onRecvSEIMsg(long l, String s) {

    }

    @Override
    public void onAudioRecording(int i, String s) {

    }


    @Override
    public void onError(int code) {}

    @Override
    public void onWarning(int code) {}

    @Override
    public void onMediaRelayStatesChange(int i, String s) {

    }

    @Override
    public void onMediaRelayReceiveEvent(int i, int i1, String s) {

    }

    @Override
    public void onUserSubStreamAudioStart(long l) {

    }

    @Override
    public void onUserSubStreamAudioStop(long l) {

    }

    @Override
    public void onUserSubStreamAudioMute(long l, boolean b) {

    }


//    @Override
//    public void onLocalPublishFallbackToAudioOnly(boolean b, NERtcVideoStreamType neRtcVideoStreamType) {
//
//    }
//
//    @Override
//    public void onRemoteSubscribeFallbackToAudioOnly(long l, boolean b, NERtcVideoStreamType neRtcVideoStreamType) {
//
//    }
}
