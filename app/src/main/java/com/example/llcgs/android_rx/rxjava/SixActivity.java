package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxbus.RxBus;
import com.example.llcgs.android_rx.rxbus.event.UserEvent;
import com.example.llcgs.android_rx.rxlifecycle.ActivityLifeCycleEvent;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;
import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.SixActivity
 *
 * @author liulongchao
 * @since 2017/6/13
 * <p>
 * RxJava 操作符--结合操作
 * <p>
 * 1.and/then/when-- 感觉很鸡肋
 * 2.combinelatest
 * 3.join
 * 4.merge
 * 5.startwith
 * 6.switch
 * 8.zip
 */


public class SixActivity extends BaseActivity {

    private String[] nbaArray = null;
    private EditText editText4, editText5, editText6, editText7;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);

        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        editText7 = (EditText) findViewById(R.id.editText7);

        RxBus.getInstance().tObservable(UserEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        UserEvent userEvent = (UserEvent) o;
                        nbaArray = userEvent.getArray();
                    }
                });


        Observable.combineLatest(RxTextView.textChanges(editText4), RxTextView.textChanges(editText5),
                RxTextView.textChanges(editText6), RxTextView.textChanges(editText7),
                new Function4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(@NonNull CharSequence charSequence, @NonNull CharSequence charSequence2, @NonNull CharSequence charSequence3, @NonNull CharSequence charSequence4) throws Exception {
                return null;
            }
        });

    }
}
