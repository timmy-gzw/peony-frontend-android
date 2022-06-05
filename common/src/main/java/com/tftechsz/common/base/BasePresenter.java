package com.tftechsz.common.base;

import com.google.gson.Gson;
import com.tftechsz.common.http.BaseResponse;

import org.reactivestreams.Publisher;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class BasePresenter<T extends MvpView> implements IPresenter<T> {

    protected final static int PAGE_SIZE = 20;

    Gson gson;
    protected CompositeDisposable mCompositeDisposable;

    public BasePresenter() {
        gson = new Gson();
    }


    protected Reference<T> mViewRef;

    @Override
    public void attachView(T mvpView) {
        mViewRef = new WeakReference<T>(mvpView);
        mCompositeDisposable = new CompositeDisposable();
    }

    public void addNet(Disposable d) {
        if (null == mCompositeDisposable) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(d);
    }

    public static <T> FlowableTransformer<T, T> applySchedulers() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public <T> FlowableTransformer<T, T> io_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> FlowableTransformer<BaseResponse<T>, T> handleResult() {
        return upstream -> upstream.flatMap((Function<BaseResponse<T>, Flowable<T>>) response -> {
            if (response.getCode() == 0) {
                return createData(response.getData());
            } else {
                String des = "网络出错，请稍后重试！";
                return Flowable.error(new Throwable(des));
            }
        }).compose(io_main());
    }

    private <T> Flowable<T> createData(final T t) {
        return Flowable.create(subscriber -> {
            try {
                subscriber.onNext(t);
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }, BackpressureStrategy.ERROR);
    }


    public RequestBody createRequestBody(Map map) {
        String json = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        return requestBody;
    }

    /**
     * 通过getView获取mView 可以有效避免context内存泄露
     *
     * @return
     */
    protected T getView() {
        if (null != mViewRef)
            return mViewRef.get();
        return null;
    }

    @Override
    public void detachView() {
        clearHttpRequest();
        if (null != mViewRef && mViewRef.get() != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public void clearHttpRequest() {
        if (null != mCompositeDisposable && mCompositeDisposable.size() > 0) {
            if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
                mCompositeDisposable.dispose();
                mCompositeDisposable.clear();
            }
        }
    }

    public boolean isViewAttached() {
        return getView() != null;
    }


}
