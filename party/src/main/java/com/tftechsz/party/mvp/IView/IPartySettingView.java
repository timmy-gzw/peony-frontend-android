package com.tftechsz.party.mvp.IView;

import android.content.Context;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.party.entity.PartyEditBean;
import com.tftechsz.party.entity.PartyManagerBgBean;

import java.util.List;

public interface IPartySettingView extends MvpView {


    void editData(PartyEditBean data);

    void addBgs(List<PartyManagerBgBean> data);

    void setImgCover(String data, String value);

    void commit();

    void closePartySuccess();

    Context getContext();

    void checkStatusCallBack(int status);
}
