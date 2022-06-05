package com.tftechsz.mine.mvp.IView;

import com.tftechsz.common.base.MvpView;

public interface IAlipayAuthView extends MvpView {


    void userCertifySuccess(String url,String certifyId);


    void checkCertifySuccess(Boolean data);

}
