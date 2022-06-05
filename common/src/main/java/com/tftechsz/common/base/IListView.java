package com.tftechsz.common.base;

import java.util.List;

import io.reactivex.Flowable;

public interface IListView<T> extends MvpView {
    void setData(List<T> datas, int page);
    Flowable httpRequest();
}
