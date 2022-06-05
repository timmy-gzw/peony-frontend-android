package com.tftechsz.family.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.MasterChangeStatus;

public interface IFamilySettingView extends MvpView {


    void updateFamilyIconSuccess(Boolean data);

    void applyLeaveSuccess(Boolean data);

    void muteAllSuccess(Boolean data,int mute);

    void masterChangeStatusSuccess(MasterChangeStatus data);
}
