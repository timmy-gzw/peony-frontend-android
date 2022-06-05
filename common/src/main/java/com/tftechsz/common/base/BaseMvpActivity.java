package com.tftechsz.common.base;

import android.os.Bundle;

import androidx.annotation.Nullable;


public abstract class BaseMvpActivity<V extends MvpView, P extends BasePresenter<V>>  extends BaseActivity{
    protected final static int PAGE_SIZE = 20;

    protected P p;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p = initPresenter();
        if (null != p)
            p.attachView((V) this);
        initView(savedInstanceState);
        initData();
    }

    protected P getP() {
        if (null == p) {
            p = initPresenter();
        }
        return p;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != p) {
            p.detachView();
            p = null;
        }
    }

    public abstract P initPresenter();


}
