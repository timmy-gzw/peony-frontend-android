package com.tftechsz.common.manager;

import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.entity.IntimacyEntity;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DbManager {
    private DbManager() {
    }

    public static DbManager getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final DbManager instance = new DbManager();
    }

    /**
     * 插入数据
     *
     * @param intimacy
     */
    public Observable<Long> insert(IntimacyEntity intimacy) {
        return Observable.create((ObservableOnSubscribe<Long>) emitter -> {
            Long insert = DbDatabase.getInstance(BaseApplication.getInstance()).getIntimacyDao().insert(intimacy);
            emitter.onNext(insert);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 更新
     *
     * @param intimacy
     * @return
     */
    public Observable<Integer> update(IntimacyEntity intimacy) {
        return Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            int inserts = DbDatabase.getInstance(BaseApplication.getInstance()).getIntimacyDao().update(intimacy);
            emitter.onNext(inserts);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 查询
     */
    public Observable<IntimacyEntity> query(String userId, String selfId) {
        return Observable.create((ObservableOnSubscribe<IntimacyEntity>) emitter -> {
            IntimacyEntity entity = DbDatabase.getInstance(BaseApplication.getInstance()).getIntimacyDao().query(userId, selfId);
            if (entity != null) {
                emitter.onNext(entity);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 删除某一个
     *
     * @return
     */
    public Observable<Integer> delete(IntimacyEntity intimacy) {
        return Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            int inserts = DbDatabase.getInstance(BaseApplication.getInstance()).getIntimacyDao().delete(intimacy);
            emitter.onNext(inserts);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}

