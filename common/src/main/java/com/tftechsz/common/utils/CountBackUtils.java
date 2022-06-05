package com.tftechsz.common.utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 倒计时 工具类
 */
public class CountBackUtils {


    public Disposable disposable;
    private long time;
    private boolean isTiming = false;

    public void countBack(long time, final Callback back) {
        this.time = time;
        isTiming = false;
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (CountBackUtils.this.time < 0) {
                            back.countBacking(CountBackUtils.this.time);
                        } else {
                            --CountBackUtils.this.time;
                            isTiming = true;
                            if (CountBackUtils.this.time <= 0) {
                                isTiming = false;
                                disposable.dispose();
                                disposable = null;
                                back.finish();
                                return;
                            }
                            back.countBacking(CountBackUtils.this.time);
                        }
                    }
                });
    }


    public boolean isTiming() {
        return isTiming;
    }

    public void updateTime(int time) {
        this.time = time;
    }

    public interface Callback {
        void countBacking(long time);

        void finish();
    }

    public interface Callback2 {
        void countBacking(long time);

        void finish();
    }

    public void destroy() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
        if (null != disposable2 && !disposable2.isDisposed()) {
            disposable2.dispose();
            disposable2 = null;
        }
    }

    public Disposable disposable2;
    private long time2;
    private boolean isTiming2 = false;

    /**
     * 一个activity两个倒计时处理
     */
    public void countBack2(long time, final CountBackUtils.Callback2 back) {
        this.time2 = time;
        isTiming2 = false;
        if (disposable2 != null) {
            disposable2.dispose();
            disposable2 = null;
        }
        disposable2 = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (CountBackUtils.this.time2 < 0) {
                            back.countBacking(CountBackUtils.this.time2);
                        } else {
                            --CountBackUtils.this.time2;
                            isTiming2 = true;
                            if (CountBackUtils.this.time2 <= 0) {
                                isTiming2 = false;
                                disposable2.dispose();
                                disposable2 = null;
                                back.finish();
                                return;
                            }
                            back.countBacking(CountBackUtils.this.time2);
                        }
                    }
                });
    }

}
