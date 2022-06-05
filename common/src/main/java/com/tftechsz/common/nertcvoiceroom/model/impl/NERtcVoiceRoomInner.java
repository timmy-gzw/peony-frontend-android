package com.tftechsz.common.nertcvoiceroom.model.impl;

import com.netease.nimlib.sdk.RequestCallback;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nim.model.NERTCVideoCall;

import java.util.List;

public abstract class NERtcVoiceRoomInner extends NERTCVideoCall {
    public abstract void updateSeat(VoiceRoomSeat seat);

    public abstract VoiceRoomSeat getSeat(int index);

    public abstract void sendSeatEvent(VoiceRoomSeat seat, boolean enter);

    public abstract void sendSeatUpdate(VoiceRoomSeat seat, RequestCallback<Void> callback);

    public abstract void fetchSeats(final RequestCallback<List<VoiceRoomSeat>> callback);

    public abstract void refreshSeats();

    public abstract boolean isInitial();
}
