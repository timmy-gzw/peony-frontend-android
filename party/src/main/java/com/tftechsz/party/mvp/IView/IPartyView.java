package com.tftechsz.party.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.party.entity.dto.PartyDto;

public interface IPartyView extends MvpView {


    void getPartyListSuccess(PartyDto data);

    void getPartyListFail();


}
