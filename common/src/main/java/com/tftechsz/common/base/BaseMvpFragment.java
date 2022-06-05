package com.tftechsz.common.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseMvpFragment<V extends MvpView, P extends BasePresenter<V>> extends BaseFragment {

    protected P p;


    protected static String TAG = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        p = initPresenter();
        if (null != p)
            p.attachView((V) this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract P initPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != p)
            p.detachView();
    }

}
