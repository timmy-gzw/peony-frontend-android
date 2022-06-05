package com.tftechsz.common.utils;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ExceptionHandler;
import com.tftechsz.common.http.ResponseThrowable;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Description: <Rx适配器><br>
 */
public class RxUtil {
    /**
     * 生命周期绑定
     *
     * @param lifecycle Activity
     */
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull LifecycleProvider lifecycle) {
        if (lifecycle != null) {
            return lifecycle.bindUntilEvent(ActivityEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("context not the LifecycleProvider type");
        }
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

    /**
     * 线程调度器
     */
    public static ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static ObservableTransformer exceptionTransformer() {

        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable observable) {
                return observable
                        .map(new HandleFuc())
                        .onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }

    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable t) {
            ResponseThrowable exception = ExceptionHandler.handleException(t);
            if(exception.code ==  ExceptionHandler.SYSTEM_ERROR.TIMEOUT_ERROR ){
                ToastUtil.showToast(BaseApplication.getInstance(),"网络不给力哦！");
            }
            return Observable.error(exception);
        }
    }

    private static class HandleFuc implements Function<Object, Object> {

        @Override
        public Object apply(Object o) throws Exception {
            if(o instanceof BaseResponse){
                BaseResponse respDTO = (BaseResponse) o;
                if(respDTO.getCode() != ExceptionHandler.APP_ERROR.SUCC){
                }
            }
            return o;
        }
    }

}
