package com.example.llcgs.android_rx.rxlifecycle;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * com.example.llcgs.android_rx.base.BaseActivity
 *
 * @author liulongchao
 * @since 2017/6/12
 */


public class BaseActivity extends AppCompatActivity implements LifecycleProvider<LifeCycleEvent>{

    private CompositeDisposable compositeDisposable;
    protected final BehaviorSubject<LifeCycleEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
    }

    public void addDisposable(Disposable disposable) {
        compositeDisposable = (compositeDisposable == null) ? new CompositeDisposable() : compositeDisposable;
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityLifeCycleEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lifecycleSubject != null) {
            lifecycleSubject.onNext(ActivityLifeCycleEvent.RESUME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (lifecycleSubject != null) {
            lifecycleSubject.onNext(ActivityLifeCycleEvent.PAUSE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lifecycleSubject != null) {
            lifecycleSubject.onNext(ActivityLifeCycleEvent.STOP);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lifecycleSubject != null) {
            lifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }

    @Nonnull
    @Override
    @CheckResult
    public Observable<LifeCycleEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Nonnull
    @Override
    @CheckResult
    public <T> LifecycleTransformer<T> bindUntilEvent(LifeCycleEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject,event);
    }

    @Nonnull
    @Override
    @CheckResult
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bind(lifecycleSubject, LifecycleHelper.activityLifecycle());
    }
}
