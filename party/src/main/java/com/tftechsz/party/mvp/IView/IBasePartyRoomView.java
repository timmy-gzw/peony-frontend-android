package com.tftechsz.party.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.party.entity.dto.JoinPartyDto;

import java.util.List;

public interface IBasePartyRoomView extends MvpView {

    void joinPartySuccess(JoinPartyDto data, String type);

    void leaveSeatSuccess(int position);

    void finishActivity();

    void stopPkSuccess();

    void getGiftSuccess(String id, List<VoiceRoomSeat> users, boolean clickOnSeatDown);

    void dismissDialogs(int tag, int value);

     void choosePictureSuccess();
    //转盘开关
    void switchWheel(Integer data);
}
