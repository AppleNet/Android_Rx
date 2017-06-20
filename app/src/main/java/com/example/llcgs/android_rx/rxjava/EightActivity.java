package com.example.llcgs.android_rx.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.llcgs.android_rx.R;
import com.example.llcgs.android_rx.rxlifecycle.ActivityLifeCycleEvent;
import com.example.llcgs.android_rx.rxlifecycle.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * com.example.llcgs.android_rx.rxjava.EightActivity
 *
 * @author liulongchao
 * @since 2017/6/20
 *
 *  RxJava操作符--辅助操作
 *  1.Delay
 *  2.DO
 *  3.Materialize/Dematerialize
 *  4.ObserverOn
 *  5.Serialize
 *  6.Subscribe
 *  7.SubscribeOn
 *  8.TimeInterval
 *  9.Timeout
 *  10.Timestamp
 *  11.Using
 *  12.To
 *
 */
public class EightActivity extends BaseActivity {

    private static final String TAG = EightActivity.class.getSimpleName();

    private String [] nbaArray = new String[]{
            "Jodn","Wade","James","Bosh","Kobe","McGrady","answer"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);

        /**
         * Delay
         *  延迟一段指定的时间再发射来自Observable的发射物
         * Delay操作符让原始Observable在发射每项数据之前都暂停一段指定的时间段。
         * 效果是Observable发射的数据项在时间上向前整体平移了一个增量
         *
         * 注意：delay不会平移onError通知，它会立即将这个通知传递给订阅者，同时丢弃任何待发射的onNext通知。
         * 然而它会平移一个onCompleted通知。
         *
         * */
        Log.d(TAG, "currentTime:"+System.currentTimeMillis());
        Observable.just("Jodn","Wade","James","Bosh","Kobe","McGrady","answer")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                })
                .compose(bindUntilEvent(ActivityLifeCycleEvent.DESTROY))
                // 延迟两秒发射
                .delay(2, TimeUnit.SECONDS)
                /*.flatMap(new Function<Object, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(@NonNull Object o) throws Exception {
                        return Observable.just(o);
                    }
                })*/
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Log.d(TAG, "currentTime:"+System.currentTimeMillis());
                        Log.d(TAG, "nba star："+o.toString());
                    }
                });

        /**
         * Do
         *  注册一个动作作为原始Observable生命周期事件的一种占位符
         *
         *  doOnEach：为 Observable注册这样一个回调，当Observable没发射一项数据就会调用它一次，包括onNext、onError和 onCompleted
         *  doOnNext：只有执行onNext的时候会被调用
         *  doOnSubscribe： 当观察者（Sunscriber）订阅Observable时就会被调用
         *  doOnUnsubscribe： 当观察者取消订阅Observable时就会被调用；Observable通过onError或者onCompleted结束时，会反订阅所有的Subscriber
         *  doOnCompleted：当Observable 正常终止调用onCompleted时会被调用。
         *  doOnError： 当Observable 异常终止调用onError时会被调用。
         *  doOnTerminate： 当Observable 终止之前会被调用，无论是正常还是异常终止
         *  finallyDo： 当Observable 终止之后会被调用，无论是正常还是异常终止
         *
         * */
        Observable.fromArray(nbaArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // 当观察者取消订阅Observable时就会被调用；Observable通过onError或者onCompleted结束时，会反订阅所有的Subscriber
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        addDisposable(disposable);
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "nba star:doOnSubscribe-"+s);
            }
        });

        /**
         * Materialize/Dematerialize 感觉没什么卵用
         *     materialize将来自原始Observable的通知（onNext/onError/onComplete）都转换为一个Notification对象，然后再按原来的顺序一次发射出去。
         *     Dematerialize操作符是Materialize的逆向过程，它将Materialize转换的结果还原成它原本的形式（ 将Notification对象还原成Observable的通知）
         * */
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("where amazing happens");
                e.onComplete();
            }
        }).materialize().subscribe(new Consumer<Notification<String>>() {
            @Override
            public void accept(@NonNull Notification<String> stringNotification) throws Exception {
                Log.d(TAG, "座右铭："+stringNotification.getValue());
            }
        });

        /**
         * Serialize
         *   强制一个Observable连续调用并保证行为正确
         *
         * */

    }
}
