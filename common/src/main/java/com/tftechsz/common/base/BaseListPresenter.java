package com.tftechsz.common.base;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BaseListPresenter<T> extends BasePresenter<IListView<T>> {
    private Flowable listObservable;
    private CompositeDisposable container = new CompositeDisposable();

    public BaseListPresenter(Flowable o) {
        listObservable = o;
    }

    public void getListData(int page) {
        if (null == listObservable) {
            getView().setData(new ArrayList<>(), 1);
            return;
        }
        Disposable subscribe = getView().httpRequest()
                .compose(io_main())
                .compose(handleResult())
                .subscribe((Consumer<List<T>>) data -> {
                    if (null == getView()) return;
                    if (null != data && null != getView()) {
                        getView().setData(data, page);
                    } else {
                        if (null != getView())
                            getView().setData(new ArrayList<>(), 1);
                    }
                }, throwable -> {
                    if (null != getView())
                        getView().setData(new ArrayList<>(), 1);
                });
        container.add(subscribe);
    }

    @Override
    public void detachView() {
        super.detachView();
        container.dispose();
    }
}
