package com.tftechsz.moment.mvp.IView;

import com.tftechsz.common.base.MvpView;

public interface ITrendView extends MvpView {

    void sendTrendSuccess(Boolean data);

    void beforeCheckSuccess();
}
