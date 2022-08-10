package com.tftechsz.main.mvp.IView;

import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.entity.SignInBean;
import com.tftechsz.common.entity.SignInSuccessBean;

public interface IMainView extends MvpView {

    void signListSucceeded(SignInBean data);

    void signListFail();

    void onSignInResult(SignInSuccessBean data);
}
