package com.tftechsz.main.mvp.IView;

import com.tftechsz.home.entity.SignInBean;
import com.tftechsz.common.base.MvpView;

public interface IMainView extends MvpView {

    void signListSucceeded(SignInBean data);

    void signListFail();

    void signInSucceeded();
}
