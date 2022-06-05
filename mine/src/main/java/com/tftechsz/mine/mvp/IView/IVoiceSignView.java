package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;

public interface IVoiceSignView extends MvpView {
    void uploadSuccess(String msg);

    void addAccostSettingSuccess();

    void addAccostSettingError();
}
