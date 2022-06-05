package com.tftechsz.family.mvp.IView;

import com.tftechsz.common.base.MvpView;

public interface IEditFamilyInfoView extends MvpView {


    void editFamilyInfoSuccess(String data);

    void dissolutionFamilySuccess(Boolean data);

    void exitFamilySuccess(Boolean data);

    void closeVoice(String name);

}
