package com.tftechsz.common.base;

public interface IPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
