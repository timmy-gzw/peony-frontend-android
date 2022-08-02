package com.tftechsz.main.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.home.entity.SignInBean;
import com.tftechsz.home.entity.SignInSuccessBean;

public interface IMainView extends MvpView {

    void signListSucceeded(SignInBean data);

    void signListFail();

    void signInSuccess(SignInSuccessBean data);

    void signInFail();
}
