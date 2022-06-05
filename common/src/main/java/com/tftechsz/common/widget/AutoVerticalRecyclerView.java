package com.tftechsz.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class AutoVerticalRecyclerView extends RecyclerView {

    private Disposable mAutoTask;

    public AutoVerticalRecyclerView(@NonNull @NotNull Context context) {
        super(context);
    }

    public AutoVerticalRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoVerticalRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    public void start(){
        if (mAutoTask != null && mAutoTask.isDisposed()) {
            mAutoTask.dispose();
        }
        mAutoTask = Observable.interval(3000, 100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        smoothScrollBy(0, 20);
                    }
                });
    }

    public void stop(){
        if (mAutoTask != null && mAutoTask.isDisposed()) {
            mAutoTask.dispose();
            mAutoTask = null;
        }
    }


}
