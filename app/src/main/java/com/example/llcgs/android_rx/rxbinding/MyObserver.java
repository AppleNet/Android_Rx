package com.example.llcgs.android_rx.rxbinding;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * com.example.llcgs.android_rx.rxbinding.MyObserver
 *
 * @author liulongchao
 * @since 2017/6/8
 */


public abstract class MyObserver<T> implements Observer<T> {


    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public abstract void onNext(@NonNull T t);

    @Override
    public void onError(@NonNull Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
